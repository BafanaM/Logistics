package com.bafanam.logistics.domain.port

/**
 * Outbound port for sync — implemented by REST/GraphQL or mocks in the data layer.
 */
fun interface OutboundSyncClient {
    suspend fun pushConsignment(businessKey: String, rawPayload: String): Result<Unit>
}
