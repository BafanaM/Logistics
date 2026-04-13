package com.bafanam.logistics.data.repository

import androidx.test.core.app.ApplicationProvider
import com.bafanam.logistics.data.local.ConsignmentDatabase
import com.bafanam.logistics.domain.model.ConsignmentRecordStatus
import com.bafanam.logistics.domain.model.InboundConsignment
import com.bafanam.logistics.domain.model.IngestOutcome
import com.bafanam.logistics.domain.model.SyncQueueOutcome
import com.bafanam.logistics.domain.model.ValidationState
import com.bafanam.logistics.domain.port.OutboundSyncClient
import com.bafanam.logistics.domain.repository.ConsignmentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ConsignmentRepositoryImplTest {

    private lateinit var db: ConsignmentDatabase
    private lateinit var repo: ConsignmentRepository

    @Before
    fun setUp() {
        db = ConsignmentDatabase.createInMemory(ApplicationProvider.getApplicationContext())
        repo = ConsignmentRepositoryImpl(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun duplicateBusinessKeyIsSkipped() = runBlocking {
        val inbound = validInbound("DUP-1")
        assertEquals(IngestOutcome.Inserted, repo.ingestInboundRecord(inbound))
        assertEquals(IngestOutcome.DuplicateSkipped, repo.ingestInboundRecord(inbound))
    }

    @Test
    fun invalidRecordNotQueuedAndHasReason() = runBlocking {
        val inbound = InboundConsignment(
            consignmentId = "INV-1",
            customerCode = "",
            itemCount = 1,
            lastUpdated = "t",
            inboundStatus = "OPEN",
            payloadHash = null,
        )
        assertEquals(IngestOutcome.Inserted, repo.ingestInboundRecord(inbound))
        val rows = repo.observeRecordsWithQueue().first()
        val row = rows.single()
        assertEquals(ConsignmentRecordStatus.INVALID, row.record.status)
        assertEquals(ValidationState.INVALID, row.record.validationState)
        assertNotNull(row.record.failureReason)
    }

    @Test
    fun validatedMovesToQueuedAndSyncs() = runBlocking {
        assertEquals(IngestOutcome.Inserted, repo.ingestInboundRecord(validInbound("OK-1")))
        assertEquals(1, repo.enqueueAllValidated())
        val client = object : OutboundSyncClient {
            override suspend fun pushConsignment(businessKey: String, rawPayload: String) = Result.success(Unit)
        }
        assertEquals(1, repo.processSyncQueue(client))
        val rows = repo.observeRecordsWithQueue().first()
        assertEquals(ConsignmentRecordStatus.SYNCED, rows.single().record.status)
        assertEquals(SyncQueueOutcome.SUCCESS, rows.single().queue!!.outcome)
    }

    @Test
    fun syncFailureThenRetry() = runBlocking {
        assertEquals(IngestOutcome.Inserted, repo.ingestInboundRecord(validInbound("FAILKEY-1")))
        assertEquals(1, repo.enqueueAllValidated())
        val failing = object : OutboundSyncClient {
            override suspend fun pushConsignment(businessKey: String, rawPayload: String) =
                if (businessKey.contains("FAIL")) {
                    Result.failure(IllegalStateException("boom"))
                } else {
                    Result.success(Unit)
                }
        }
        assertEquals(1, repo.processSyncQueue(failing))
        val failedRow = repo.observeRecordsWithQueue().first().single()
        assertEquals(ConsignmentRecordStatus.FAILED, failedRow.record.status)
        assertEquals(SyncQueueOutcome.FAILED, failedRow.queue!!.outcome)

        assertTrue(repo.retryFailed(failedRow.record.id))
        val succeeding = object : OutboundSyncClient {
            override suspend fun pushConsignment(businessKey: String, rawPayload: String) = Result.success(Unit)
        }
        assertEquals(1, repo.processSyncQueue(succeeding))
        val after = repo.observeRecordsWithQueue().first().single()
        assertEquals(ConsignmentRecordStatus.SYNCED, after.record.status)
        assertTrue(after.record.retryCount >= 1)
    }

    private fun validInbound(id: String) = InboundConsignment(
        consignmentId = id,
        customerCode = "C",
        itemCount = 2,
        lastUpdated = "t",
        inboundStatus = "OPEN",
        payloadHash = "h",
    )
}
