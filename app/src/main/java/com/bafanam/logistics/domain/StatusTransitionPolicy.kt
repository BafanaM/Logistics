package com.bafanam.logistics.domain

import com.bafanam.logistics.domain.model.ConsignmentRecordStatus
import com.bafanam.logistics.domain.model.ValidationState

/**
 * Documents allowed transitions for tests and invariants (repository enforces at write time).
 */
object StatusTransitionPolicy {

    fun afterValidation(
        validationState: ValidationState,
        current: ConsignmentRecordStatus,
    ): ConsignmentRecordStatus {
        require(current == ConsignmentRecordStatus.NEW) { "validation runs only from NEW, was $current" }
        return when (validationState) {
            ValidationState.VALID -> ConsignmentRecordStatus.VALIDATED
            ValidationState.INVALID -> ConsignmentRecordStatus.INVALID
            ValidationState.PENDING -> error("validation must resolve PENDING before transition")
        }
    }

    fun canQueue(status: ConsignmentRecordStatus, validationState: ValidationState): Boolean =
        status == ConsignmentRecordStatus.VALIDATED && validationState == ValidationState.VALID

    fun canMarkSynced(status: ConsignmentRecordStatus): Boolean =
        status == ConsignmentRecordStatus.QUEUED

    fun canMarkSyncFailed(status: ConsignmentRecordStatus): Boolean =
        status == ConsignmentRecordStatus.QUEUED

    fun canRetrySync(status: ConsignmentRecordStatus): Boolean =
        status == ConsignmentRecordStatus.FAILED
}
