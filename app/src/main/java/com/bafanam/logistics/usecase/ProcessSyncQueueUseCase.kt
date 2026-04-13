package com.bafanam.logistics.usecase

import com.bafanam.logistics.domain.port.OutboundSyncClient
import com.bafanam.logistics.domain.repository.ConsignmentRepository

class ProcessSyncQueueUseCase(
    private val repository: ConsignmentRepository,
    private val syncClient: OutboundSyncClient,
) {
    suspend operator fun invoke(): Int = repository.processSyncQueue(syncClient)
}
