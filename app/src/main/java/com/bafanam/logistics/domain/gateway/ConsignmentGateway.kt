package com.bafanam.logistics.domain.gateway

import com.bafanam.logistics.domain.model.InboundConsignment

/**
 * Inbound port — implemented by mock or real network/GraphQL clients in the data layer.
 */
interface ConsignmentGateway {
    suspend fun fetchInboundRecords(): List<InboundConsignment>
}
