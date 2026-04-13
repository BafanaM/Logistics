package com.bafanam.logistics.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bafanam.logistics.data.local.dao.LocalConsignmentDao
import com.bafanam.logistics.data.local.dao.SyncQueueDao
import com.bafanam.logistics.data.local.entity.LocalConsignmentEntity
import com.bafanam.logistics.data.local.entity.SyncQueueItemEntity

@Database(
    entities = [
        LocalConsignmentEntity::class,
        SyncQueueItemEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(ConsignmentTypeConverters::class)
abstract class ConsignmentDatabase : RoomDatabase() {

    abstract fun localConsignmentDao(): LocalConsignmentDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        fun createPersistent(context: Context): ConsignmentDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                ConsignmentDatabase::class.java,
                "logistics.db",
            ).fallbackToDestructiveMigration().build()

        fun createInMemory(context: Context): ConsignmentDatabase =
            Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                ConsignmentDatabase::class.java,
            ).allowMainThreadQueries().build()
    }
}
