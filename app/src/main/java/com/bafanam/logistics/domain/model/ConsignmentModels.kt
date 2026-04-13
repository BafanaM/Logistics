package com.bafanam.logistics.domain.model

/**
 * Inbound shape from upstream (mock gateway). Fields may be null before validation.
 */
data class InboundConsignment(
    val consignmentId: String?,
    val customerCode: String?,
    val itemCount: Int?,
    val lastUpdated: String?,
    val inboundStatus: String?,
    val payloadHash: String?,
)

enum class ValidationState {
    PENDING,
    VALID,
    INVALID,
}

/**
 * Lifecycle: NEW → VALIDATED | INVALID → QUEUED → SYNCED | FAILED.
 * INVALID denotes failed validation (not queued). FAILED is sync failure and remains retryable.
 */
enum class ConsignmentRecordStatus {
    NEW,
    VALIDATED,
    INVALID,
    QUEUED,
    SYNCED,
    FAILED,
}

enum class SyncQueueOutcome {
    PENDING,
    SUCCESS,
    FAILED,
}

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reasons: List<String>) : ValidationResult()
}

data class LocalConsignmentRecord(
    val id: Long,
    val businessKey: String,
    val rawPayload: String,
    val validationState: ValidationState,
    val status: ConsignmentRecordStatus,
    val failureReason: String?,
    val retryCount: Int,
)

data class SyncQueueEntry(
    val id: Long,
    val recordId: Long,
    val queuedAt: Long,
    val attemptCount: Int,
    val lastAttemptAt: Long?,
    val outcome: SyncQueueOutcome,
)

enum class IngestOutcome {
    Inserted,
    DuplicateSkipped,
}

data class IntakeSummary(
    val processed: Int,
    val duplicatesSkipped: Int,
    val inserted: Int,
)

data class ConsignmentListRow(
    val record: LocalConsignmentRecord,
    val queue: SyncQueueEntry?,
)
