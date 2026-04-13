package com.bafanam.logistics.domain

import com.bafanam.logistics.domain.model.ConsignmentRecordStatus
import com.bafanam.logistics.domain.model.ValidationState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StatusTransitionPolicyTest {

    @Test
    fun validatedRowCanQueueWhenValid() {
        assertTrue(
            StatusTransitionPolicy.canQueue(
                ConsignmentRecordStatus.VALIDATED,
                ValidationState.VALID,
            ),
        )
    }

    @Test
    fun invalidValidationCannotQueue() {
        assertFalse(
            StatusTransitionPolicy.canQueue(
                ConsignmentRecordStatus.INVALID,
                ValidationState.INVALID,
            ),
        )
    }

    @Test
    fun queuedCanCompleteOrFail() {
        assertTrue(StatusTransitionPolicy.canMarkSynced(ConsignmentRecordStatus.QUEUED))
        assertTrue(StatusTransitionPolicy.canMarkSyncFailed(ConsignmentRecordStatus.QUEUED))
    }

    @Test
    fun onlyFailedCanRetrySync() {
        assertTrue(StatusTransitionPolicy.canRetrySync(ConsignmentRecordStatus.FAILED))
        assertFalse(StatusTransitionPolicy.canRetrySync(ConsignmentRecordStatus.QUEUED))
        assertFalse(StatusTransitionPolicy.canRetrySync(ConsignmentRecordStatus.SYNCED))
    }
}
