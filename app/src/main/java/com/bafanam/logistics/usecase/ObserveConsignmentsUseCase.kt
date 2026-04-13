package com.bafanam.logistics.usecase

import com.bafanam.logistics.domain.model.ConsignmentListRow
import com.bafanam.logistics.domain.repository.ConsignmentRepository
import kotlinx.coroutines.flow.Flow

/**
 * Read-side use case — ViewModel observes through this instead of the repository (MVVM + layering).
 */
class ObserveConsignmentsUseCase(
    private val repository: ConsignmentRepository,
) {
    operator fun invoke(): Flow<List<ConsignmentListRow>> = repository.observeRecordsWithQueue()
}
