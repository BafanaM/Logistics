package com.bafanam.logistics.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bafanam.logistics.data.local.entity.ConsignmentWithQueueRelation
import com.bafanam.logistics.data.local.entity.LocalConsignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalConsignmentDao {

    @Query("SELECT EXISTS(SELECT 1 FROM local_consignment WHERE businessKey = :businessKey LIMIT 1)")
    suspend fun existsByBusinessKey(businessKey: String): Boolean

    @Insert
    suspend fun insert(entity: LocalConsignmentEntity): Long

    @Update
    suspend fun update(entity: LocalConsignmentEntity)

    @Query("SELECT * FROM local_consignment WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LocalConsignmentEntity?

    @Query("SELECT * FROM local_consignment WHERE status = 'VALIDATED' ORDER BY id ASC")
    suspend fun listValidated(): List<LocalConsignmentEntity>

    @Transaction
    @Query("SELECT * FROM local_consignment ORDER BY id ASC")
    fun observeAllWithQueue(): Flow<List<ConsignmentWithQueueRelation>>
}
