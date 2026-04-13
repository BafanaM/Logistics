package com.bafanam.logistics.data.local

import androidx.room.TypeConverter
import com.bafanam.logistics.domain.model.ConsignmentRecordStatus
import com.bafanam.logistics.domain.model.SyncQueueOutcome
import com.bafanam.logistics.domain.model.ValidationState

class ConsignmentTypeConverters {

    @TypeConverter
    fun validationStateToString(v: ValidationState): String = v.name

    @TypeConverter
    fun stringToValidationState(s: String): ValidationState = ValidationState.valueOf(s)

    @TypeConverter
    fun recordStatusToString(v: ConsignmentRecordStatus): String = v.name

    @TypeConverter
    fun stringToRecordStatus(s: String): ConsignmentRecordStatus = ConsignmentRecordStatus.valueOf(s)

    @TypeConverter
    fun syncOutcomeToString(v: SyncQueueOutcome): String = v.name

    @TypeConverter
    fun stringToSyncOutcome(s: String): SyncQueueOutcome = SyncQueueOutcome.valueOf(s)
}
