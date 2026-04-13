package com.bafanam.logistics.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ConsignmentWithQueueRelation(
    @Embedded val consignment: LocalConsignmentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recordId",
    )
    val queueItems: List<SyncQueueItemEntity>,
)
