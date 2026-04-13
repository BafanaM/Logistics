package com.bafanam.logistics.domain.repository

import com.bafanam.logistics.domain.model.ConsignmentListRow
import com.bafanam.logistics.domain.model.InboundConsignment
import com.bafanam.logistics.domain.model.IngestOutcome
import com.bafanam.logistics.domain.port.OutboundSyncClient
import kotlinx.coroutines.flow.Flow

/**
 * Persistence and sync orchestration contract — domain depends on this abstraction (DIP).
 */
interface ConsignmentRepository {
    fun observeRecordsWithQueue(): Flow<List<ConsignmentListRow>>
    suspend fun ingestInboundRecord(inbound: InboundConsignment): IngestOutcome
    suspend fun enqueueAllValidated(): Int
    suspend fun processSyncQueue(client: OutboundSyncClient): Int
    suspend fun retryFailed(recordId: Long): Boolean
}
