package com.bafanam.logistics.data.gateway

import com.bafanam.logistics.domain.model.InboundConsignment
import com.google.gson.annotations.SerializedName

/**
 * JSON keys match the inbound feed sample; maps to domain [InboundConsignment].
 */
data class InboundConsignmentJsonDto(
    @SerializedName("consignmentId") val consignmentId: String?,
    @SerializedName("customerCode") val customerCode: String?,
    @SerializedName("itemCount") val itemCount: Int?,
    @SerializedName("lastUpdated") val lastUpdated: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("payloadHash") val payloadHash: String?,
) {
    fun toDomain(): InboundConsignment = InboundConsignment(
        consignmentId = consignmentId,
        customerCode = customerCode,
        itemCount = itemCount,
        lastUpdated = lastUpdated,
        inboundStatus = status,
        payloadHash = payloadHash,
    )
}

fun InboundConsignment.toJsonDto(): InboundConsignmentJsonDto = InboundConsignmentJsonDto(
    consignmentId = consignmentId,
    customerCode = customerCode,
    itemCount = itemCount,
    lastUpdated = lastUpdated,
    status = inboundStatus,
    payloadHash = payloadHash,
)
