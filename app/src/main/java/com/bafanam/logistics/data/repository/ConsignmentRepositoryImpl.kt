package com.bafanam.logistics.data.repository

import androidx.room.withTransaction
import com.bafanam.logistics.data.gateway.toJsonDto
import com.bafanam.logistics.data.local.ConsignmentDatabase
import com.bafanam.logistics.data.local.entity.ConsignmentWithQueueRelation
import com.bafanam.logistics.data.local.entity.LocalConsignmentEntity
import com.bafanam.logistics.data.local.entity.SyncQueueItemEntity
import com.bafanam.logistics.domain.ConsignmentValidator
import com.bafanam.logistics.domain.model.ConsignmentListRow
import com.bafanam.logistics.domain.model.ConsignmentRecordStatus
import com.bafanam.logistics.domain.model.InboundConsignment
import com.bafanam.logistics.domain.model.IngestOutcome
import com.bafanam.logistics.domain.model.LocalConsignmentRecord
import com.bafanam.logistics.domain.model.SyncQueueEntry
import com.bafanam.logistics.domain.model.SyncQueueOutcome
import com.bafanam.logistics.domain.model.ValidationResult
import com.bafanam.logistics.domain.model.ValidationState
import com.bafanam.logistics.domain.port.OutboundSyncClient
import com.bafanam.logistics.domain.repository.ConsignmentRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ConsignmentRepositoryImpl(
    private val db: ConsignmentDatabase,
    private val gson: Gson = Gson(),
) : ConsignmentRepository {

    private val localDao = db.localConsignmentDao()
    private val syncDao = db.syncQueueDao()

    override fun observeRecordsWithQueue(): Flow<List<ConsignmentListRow>> =
        localDao.observeAllWithQueue().map { list -> list.map { it.toListRow() } }

    override suspend fun ingestInboundRecord(inbound: InboundConsignment): IngestOutcome =
        db.withTransaction {
            val trimmedId = inbound.consignmentId?.trim()
            if (!trimmedId.isNullOrEmpty() && localDao.existsByBusinessKey(trimmedId)) {
                return@withTransaction IngestOutcome.DuplicateSkipped
            }
            val businessKey = if (!trimmedId.isNullOrEmpty()) {
                trimmedId
            } else {
                "NO_CONSIGNMENT_ID_${UUID.randomUUID()}"
            }
            val now = System.currentTimeMillis()
            val rawPayload = gson.toJson(inbound.toJsonDto())
            val insertedId = localDao.insert(
                LocalConsignmentEntity(
                    businessKey = businessKey,
                    rawPayload = rawPayload,
                    validationState = ValidationState.PENDING,
                    status = ConsignmentRecordStatus.NEW,
                    failureReason = null,
                    retryCount = 0,
                    createdAtEpochMs = now,
                    inboundLastUpdated = inbound.lastUpdated,
                ),
            )
            val stored = localDao.getById(insertedId) ?: error("missing row after insert")
            applyValidation(stored, inbound)
            IngestOutcome.Inserted
        }

    private suspend fun applyValidation(entity: LocalConsignmentEntity, inbound: InboundConsignment) {
        when (val vr = ConsignmentValidator.validate(inbound)) {
            ValidationResult.Valid -> {
                localDao.update(
                    entity.copy(
                        validationState = ValidationState.VALID,
                        status = ConsignmentRecordStatus.VALIDATED,
                        failureReason = null,
                    ),
                )
            }
            is ValidationResult.Invalid -> {
                localDao.update(
                    entity.copy(
                        validationState = ValidationState.INVALID,
                        status = ConsignmentRecordStatus.INVALID,
                        failureReason = vr.reasons.joinToString("; "),
                    ),
                )
            }
        }
    }

    override suspend fun enqueueAllValidated(): Int = db.withTransaction {
        var count = 0
        for (entity in localDao.listValidated()) {
            if (syncDao.getByRecordId(entity.id) != null) continue
            val now = System.currentTimeMillis()
            syncDao.insert(
                SyncQueueItemEntity(
                    recordId = entity.id,
                    queuedAtEpochMs = now,
                    attemptCount = 0,
                    lastAttemptAtEpochMs = null,
                    outcome = SyncQueueOutcome.PENDING,
                ),
            )
            localDao.update(entity.copy(status = ConsignmentRecordStatus.QUEUED))
            count++
        }
        count
    }

    override suspend fun processSyncQueue(client: OutboundSyncClient): Int {
        val items = syncDao.listWorkableItems()
        var processed = 0
        for (item in items) {
            val work = db.withTransaction {
                val freshItem = syncDao.getByRecordId(item.recordId) ?: return@withTransaction null
                if (freshItem.outcome != SyncQueueOutcome.PENDING) return@withTransaction null
                val record = localDao.getById(freshItem.recordId) ?: return@withTransaction null
                if (record.status != ConsignmentRecordStatus.QUEUED) return@withTransaction null
                val now = System.currentTimeMillis()
                val inFlight = freshItem.copy(
                    attemptCount = freshItem.attemptCount + 1,
                    lastAttemptAtEpochMs = now,
                )
                syncDao.update(inFlight)
                Pair(record, inFlight)
            } ?: continue

            val (record, inFlight) = work
            val push = client.pushConsignment(record.businessKey, record.rawPayload)

            db.withTransaction {
                val latestQueue = syncDao.getByRecordId(record.id) ?: return@withTransaction
                if (latestQueue.id != inFlight.id || latestQueue.attemptCount != inFlight.attemptCount) {
                    return@withTransaction
                }
                val latestRecord = localDao.getById(record.id) ?: return@withTransaction
                if (latestRecord.status != ConsignmentRecordStatus.QUEUED) return@withTransaction
                if (push.isSuccess) {
                    syncDao.update(latestQueue.copy(outcome = SyncQueueOutcome.SUCCESS))
                    localDao.update(
                        latestRecord.copy(
                            status = ConsignmentRecordStatus.SYNCED,
                            failureReason = null,
                        ),
                    )
                } else {
                    syncDao.update(latestQueue.copy(outcome = SyncQueueOutcome.FAILED))
                    localDao.update(
                        latestRecord.copy(
                            status = ConsignmentRecordStatus.FAILED,
                            failureReason = push.exceptionOrNull()?.message ?: "Sync failed",
                        ),
                    )
                }
            }
            processed++
        }
        return processed
    }

    override suspend fun retryFailed(recordId: Long): Boolean = db.withTransaction {
        val record = localDao.getById(recordId) ?: return@withTransaction false
        if (record.status != ConsignmentRecordStatus.FAILED) return@withTransaction false
        val queue = syncDao.getByRecordId(recordId) ?: return@withTransaction false
        localDao.update(
            record.copy(
                status = ConsignmentRecordStatus.QUEUED,
                retryCount = record.retryCount + 1,
            ),
        )
        syncDao.update(
            queue.copy(
                outcome = SyncQueueOutcome.PENDING,
            ),
        )
        true
    }

    private fun ConsignmentWithQueueRelation.toListRow(): ConsignmentListRow {
        val c = consignment
        val record = LocalConsignmentRecord(
            id = c.id,
            businessKey = c.businessKey,
            rawPayload = c.rawPayload,
            validationState = c.validationState,
            status = c.status,
            failureReason = c.failureReason,
            retryCount = c.retryCount,
        )
        val q = queueItems.firstOrNull()
        val queue = q?.let {
            SyncQueueEntry(
                id = it.id,
                recordId = it.recordId,
                queuedAt = it.queuedAtEpochMs,
                attemptCount = it.attemptCount,
                lastAttemptAt = it.lastAttemptAtEpochMs,
                outcome = it.outcome,
            )
        }
        return ConsignmentListRow(record, queue)
    }
}
