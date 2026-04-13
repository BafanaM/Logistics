package com.bafanam.logistics.domain

import com.bafanam.logistics.domain.model.InboundConsignment
import com.bafanam.logistics.domain.model.ValidationResult
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsignmentValidatorTest {

    @Test
    fun validRecordPasses() {
        val r = InboundConsignment(
            consignmentId = "C1",
            customerCode = "X",
            itemCount = 1,
            lastUpdated = "t",
            inboundStatus = "OPEN",
            payloadHash = "h",
        )
        assertTrue(ConsignmentValidator.validate(r) is ValidationResult.Valid)
    }

    @Test
    fun missingFieldsFailWithReasons() {
        val r = InboundConsignment(
            consignmentId = null,
            customerCode = " ",
            itemCount = null,
            lastUpdated = null,
            inboundStatus = null,
            payloadHash = null,
        )
        val vr = ConsignmentValidator.validate(r)
        require(vr is ValidationResult.Invalid)
        assertTrue(vr.reasons.any { it.contains("consignmentId") })
        assertTrue(vr.reasons.any { it.contains("customerCode") })
        assertTrue(vr.reasons.any { it.contains("itemCount") })
        assertTrue(vr.reasons.any { it.contains("status") })
    }

    @Test
    fun negativeItemCountFails() {
        val r = InboundConsignment(
            consignmentId = "C1",
            customerCode = "X",
            itemCount = -1,
            lastUpdated = "t",
            inboundStatus = "OPEN",
            payloadHash = null,
        )
        val vr = ConsignmentValidator.validate(r)
        require(vr is ValidationResult.Invalid)
        assertTrue(vr.reasons.any { it.contains("itemCount") })
    }

    @Test
    fun zeroItemCountIsAllowed() {
        val r = InboundConsignment(
            consignmentId = "C1",
            customerCode = "X",
            itemCount = 0,
            lastUpdated = "t",
            inboundStatus = "OPEN",
            payloadHash = null,
        )
        assertTrue(ConsignmentValidator.validate(r) is ValidationResult.Valid)
    }
}
