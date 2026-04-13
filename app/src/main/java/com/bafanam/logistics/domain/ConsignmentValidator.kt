package com.bafanam.logistics.domain

import com.bafanam.logistics.domain.model.InboundConsignment
import com.bafanam.logistics.domain.model.ValidationResult

/**
 * Pure validation for unit tests and intake pipeline.
 */
object ConsignmentValidator {

    fun validate(inbound: InboundConsignment): ValidationResult {
        val reasons = mutableListOf<String>()
        if (inbound.consignmentId.isNullOrBlank()) {
            reasons += "consignmentId is required"
        }
        if (inbound.customerCode.isNullOrBlank()) {
            reasons += "customerCode is required"
        }
        if (inbound.itemCount == null) {
            reasons += "itemCount is required"
        } else if (inbound.itemCount < 0) {
            reasons += "itemCount must be non-negative"
        }
        if (inbound.inboundStatus.isNullOrBlank()) {
            reasons += "status is required"
        }
        return if (reasons.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(reasons)
    }
}
