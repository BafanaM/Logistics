package com.bafanam.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bafanam.logistics.domain.model.SyncQueueOutcome

@Entity(
    tableName = "sync_queue",
    foreignKeys = [
        ForeignKey(
            entity = LocalConsignmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["recordId"], unique = true)],
)
data class SyncQueueItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordId: Long,
    val queuedAtEpochMs: Long,
    val attemptCount: Int,
    val lastAttemptAtEpochMs: Long?,
    val outcome: SyncQueueOutcome,
)
