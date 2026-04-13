package com.bafanam.logistics.usecase

import com.bafanam.logistics.domain.gateway.ConsignmentGateway
import com.bafanam.logistics.domain.model.IngestOutcome
import com.bafanam.logistics.domain.model.IntakeSummary
import com.bafanam.logistics.domain.repository.ConsignmentRepository

class IntakeConsignmentsUseCase(
    private val gateway: ConsignmentGateway,
    private val repository: ConsignmentRepository,
) {
    suspend operator fun invoke(): IntakeSummary {
        val inbound = gateway.fetchInboundRecords()
        var duplicates = 0
        var inserted = 0
        for (record in inbound) {
            when (repository.ingestInboundRecord(record)) {
                IngestOutcome.DuplicateSkipped -> duplicates++
                IngestOutcome.Inserted -> inserted++
            }
        }
        return IntakeSummary(
            processed = inbound.size,
            duplicatesSkipped = duplicates,
            inserted = inserted,
        )
    }
}
