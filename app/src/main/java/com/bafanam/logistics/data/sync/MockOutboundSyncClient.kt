package com.bafanam.logistics.data.sync

import com.bafanam.logistics.domain.port.OutboundSyncClient
import kotlinx.coroutines.delay

/**
 * Simulates network latency. Fails when [businessKey] contains "FAIL" (case-insensitive) for demos/tests.
 */
class MockOutboundSyncClient(
    private val latencyMs: Long = 0L,
) : OutboundSyncClient {

    override suspend fun pushConsignment(businessKey: String, rawPayload: String): Result<Unit> {
        delay(latencyMs)
        return if (businessKey.contains("fail", ignoreCase = true)) {
            Result.failure(IllegalStateException("Simulated sync failure for key=$businessKey"))
        } else {
            Result.success(Unit)
        }
    }
}
