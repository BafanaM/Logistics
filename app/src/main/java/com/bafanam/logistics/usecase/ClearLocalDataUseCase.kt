package com.bafanam.logistics.usecase

import com.bafanam.logistics.domain.repository.ConsignmentRepository

class ClearLocalDataUseCase(
    private val repository: ConsignmentRepository,
) {
    suspend operator fun invoke() {
        repository.clearAllLocalData()
    }
}
