package com.bafanam.logistics.usecase

import com.bafanam.logistics.domain.repository.ConsignmentRepository

class RetryFailedConsignmentUseCase(
    private val repository: ConsignmentRepository,
) {
    suspend operator fun invoke(recordId: Long): Boolean = repository.retryFailed(recordId)
}
