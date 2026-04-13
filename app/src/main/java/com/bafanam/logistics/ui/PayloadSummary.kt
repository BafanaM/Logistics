package com.bafanam.logistics.ui

import org.json.JSONObject

/**
 * Structured fields from stored JSON for clear list rows (no Gson in the UI layer).
 */
data class ShipmentPayloadDisplay(
    val customerCode: String,
    val itemCountLine: String,
    val lastUpdated: String?,
    val upstreamStatus: String?,
)

fun parseShipmentPayloadDisplay(rawJson: String): ShipmentPayloadDisplay? =
    try {
        val o = JSONObject(rawJson)
        val customer = o.optString("customerCode", "").trim().ifBlank { null }
        val items = when {
            o.isNull("itemCount") -> null
            else -> o.optInt("itemCount", -1).takeIf { it >= 0 }
        }
        val itemLine = when (items) {
            null -> "—"
            1 -> "1 item"
            else -> "$items items"
        }
        val last = o.optString("lastUpdated", "").trim().ifBlank { null }
        val status = o.optString("status", "").trim().ifBlank { null }
        ShipmentPayloadDisplay(
            customerCode = customer ?: "—",
            itemCountLine = itemLine,
            lastUpdated = last,
            upstreamStatus = status,
        )
    } catch (_: Exception) {
        null
    }
