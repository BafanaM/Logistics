package com.bafanam.logistics.ui

import com.bafanam.logistics.domain.model.ConsignmentListRow

/**
 * Immutable screen state for the shipment list (MVVM — View renders state, ViewModel owns it).
 */
data class ConsignmentUiState(
    val rows: List<ConsignmentListRow> = emptyList(),
    val isBusy: Boolean = false,
)

/**
 * One-shot UI events (snackbar). Prefer Channel/Flow over shared String? + clear().
 */
sealed interface ConsignmentUiEvent {
    data class Message(val text: String) : ConsignmentUiEvent
}
