package com.bafanam.logistics.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bafanam.logistics.data.local.entity.SyncQueueItemEntity
import com.bafanam.logistics.domain.model.SyncQueueOutcome

@Dao
interface SyncQueueDao {

    @Insert
    suspend fun insert(entity: SyncQueueItemEntity): Long

    @Update
    suspend fun update(entity: SyncQueueItemEntity)

    @Query("SELECT * FROM sync_queue WHERE recordId = :recordId LIMIT 1")
    suspend fun getByRecordId(recordId: Long): SyncQueueItemEntity?

    @Query(
        """
        SELECT * FROM sync_queue q
        INNER JOIN local_consignment c ON c.id = q.recordId
        WHERE q.outcome = :pending AND c.status = 'QUEUED'
        ORDER BY q.queuedAtEpochMs ASC
        """,
    )
    suspend fun listWorkableItems(pending: SyncQueueOutcome = SyncQueueOutcome.PENDING): List<SyncQueueItemEntity>
}
