package com.bafanam.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bafanam.logistics.domain.model.ConsignmentRecordStatus
import com.bafanam.logistics.domain.model.ValidationState

@Entity(
    tableName = "local_consignment",
    indices = [Index(value = ["businessKey"], unique = true)],
)
data class LocalConsignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessKey: String,
    val rawPayload: String,
    val validationState: ValidationState,
    val status: ConsignmentRecordStatus,
    val failureReason: String?,
    val retryCount: Int,
    val createdAtEpochMs: Long,
    val inboundLastUpdated: String?,
)
