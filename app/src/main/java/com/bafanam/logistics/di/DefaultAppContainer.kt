package com.bafanam.logistics.di

import android.content.Context
import com.bafanam.logistics.data.gateway.MockConsignmentGateway
import com.bafanam.logistics.data.local.ConsignmentDatabase
import com.bafanam.logistics.data.repository.ConsignmentRepositoryImpl
import com.bafanam.logistics.data.sync.MockOutboundSyncClient
import com.bafanam.logistics.domain.gateway.ConsignmentGateway
import com.bafanam.logistics.domain.port.OutboundSyncClient
import com.bafanam.logistics.domain.repository.ConsignmentRepository
import com.bafanam.logistics.usecase.EnqueueValidatedConsignmentsUseCase
import com.bafanam.logistics.usecase.IntakeConsignmentsUseCase
import com.bafanam.logistics.usecase.ObserveConsignmentsUseCase
import com.bafanam.logistics.usecase.ProcessSyncQueueUseCase
import com.bafanam.logistics.usecase.RetryFailedConsignmentUseCase

class DefaultAppContainer(context: Context) : AppContainer {

    private val appContext = context.applicationContext

    private val database by lazy { ConsignmentDatabase.createPersistent(appContext) }

    private val repository: ConsignmentRepository by lazy {
        ConsignmentRepositoryImpl(database)
    }

    private val gateway: ConsignmentGateway by lazy {
        MockConsignmentGateway(appContext)
    }

    private val outboundSyncClient: OutboundSyncClient by lazy {
        MockOutboundSyncClient()
    }

    override val observeConsignments: ObserveConsignmentsUseCase by lazy {
        ObserveConsignmentsUseCase(repository)
    }

    override val intakeConsignments: IntakeConsignmentsUseCase by lazy {
        IntakeConsignmentsUseCase(gateway, repository)
    }

    override val enqueueValidated: EnqueueValidatedConsignmentsUseCase by lazy {
        EnqueueValidatedConsignmentsUseCase(repository)
    }

    override val processSyncQueue: ProcessSyncQueueUseCase by lazy {
        ProcessSyncQueueUseCase(repository, outboundSyncClient)
    }

    override val retryFailedConsignment: RetryFailedConsignmentUseCase by lazy {
        RetryFailedConsignmentUseCase(repository)
    }
}
