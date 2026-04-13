package com.bafanam.logistics.data.gateway

import android.content.Context
import com.bafanam.logistics.domain.gateway.ConsignmentGateway
import com.bafanam.logistics.domain.model.InboundConsignment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * Reads [assetFileName] from assets and parses JSON array into domain models.
 * Swappable for a real gateway that performs the same mapping after network I/O.
 */
class MockConsignmentGateway(
    private val context: Context,
    private val assetFileName: String = "sample_consignments.json",
    private val gson: Gson = Gson(),
) : ConsignmentGateway {

    override suspend fun fetchInboundRecords(): List<InboundConsignment> = withContext(Dispatchers.IO) {
        context.assets.open(assetFileName).use { input ->
            val type = object : TypeToken<List<InboundConsignmentJsonDto>>() {}.type
            val list: List<InboundConsignmentJsonDto> = gson.fromJson(InputStreamReader(input, Charsets.UTF_8), type)
            list.map { it.toDomain() }
        }
    }
}
