package com.bafanam.logistics.usecase

import com.bafanam.logistics.domain.repository.ConsignmentRepository

class EnqueueValidatedConsignmentsUseCase(
    private val repository: ConsignmentRepository,
) {
    suspend operator fun invoke(): Int = repository.enqueueAllValidated()
}
