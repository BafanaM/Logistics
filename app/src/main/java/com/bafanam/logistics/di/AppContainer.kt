package com.bafanam.logistics.di

import com.bafanam.logistics.usecase.EnqueueValidatedConsignmentsUseCase
import com.bafanam.logistics.usecase.IntakeConsignmentsUseCase
import com.bafanam.logistics.usecase.ObserveConsignmentsUseCase
import com.bafanam.logistics.usecase.ProcessSyncQueueUseCase
import com.bafanam.logistics.usecase.RetryFailedConsignmentUseCase

/**
 * Composition root — exposes framework-free use cases for presentation (DIP, testability).
 */
interface AppContainer {
    val observeConsignments: ObserveConsignmentsUseCase
    val intakeConsignments: IntakeConsignmentsUseCase
    val enqueueValidated: EnqueueValidatedConsignmentsUseCase
    val processSyncQueue: ProcessSyncQueueUseCase
    val retryFailedConsignment: RetryFailedConsignmentUseCase
}
