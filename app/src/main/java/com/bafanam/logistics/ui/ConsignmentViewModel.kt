package com.bafanam.logistics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bafanam.logistics.di.AppContainer
import com.bafanam.logistics.usecase.ClearLocalDataUseCase
import com.bafanam.logistics.usecase.EnqueueValidatedConsignmentsUseCase
import com.bafanam.logistics.usecase.IntakeConsignmentsUseCase
import com.bafanam.logistics.usecase.ObserveConsignmentsUseCase
import com.bafanam.logistics.usecase.ProcessSyncQueueUseCase
import com.bafanam.logistics.usecase.RetryFailedConsignmentUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConsignmentViewModel(
    observeConsignments: ObserveConsignmentsUseCase,
    private val intakeConsignments: IntakeConsignmentsUseCase,
    private val enqueueValidated: EnqueueValidatedConsignmentsUseCase,
    private val processSyncQueue: ProcessSyncQueueUseCase,
    private val retryFailedConsignment: RetryFailedConsignmentUseCase,
    private val clearLocalData: ClearLocalDataUseCase,
) : ViewModel() {

    private val _isBusy = MutableStateFlow(false)

    private val _events = Channel<ConsignmentUiEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val uiState: StateFlow<ConsignmentUiState> = combine(
        observeConsignments(),
        _isBusy,
    ) { rows, busy ->
        ConsignmentUiState(rows = rows, isBusy = busy)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ConsignmentUiState(),
    )

    fun runIntake() = viewModelScope.launch {
        withBusy {
            runCatching { intakeConsignments() }
                .onSuccess {
                    sendMessage(
                        "${it.inserted} new · ${it.duplicatesSkipped} duplicates skipped",
                    )
                }
                .onFailure { sendMessage(it.message ?: it.toString()) }
        }
    }

    fun runEnqueue() = viewModelScope.launch {
        withBusy {
            runCatching { enqueueValidated() }
                .onSuccess { sendMessage("$it queued") }
                .onFailure { sendMessage(it.message ?: it.toString()) }
        }
    }

    fun runSync() = viewModelScope.launch {
        withBusy {
            runCatching { processSyncQueue() }
                .onSuccess { sendMessage("$it processed") }
                .onFailure { sendMessage(it.message ?: it.toString()) }
        }
    }

    fun clearAllAndRestart() = viewModelScope.launch {
        withBusy {
            runCatching { clearLocalData() }
                .onSuccess { sendMessage("All data cleared. Tap Import orders to start again.") }
                .onFailure { sendMessage(it.message ?: it.toString()) }
        }
    }

    fun retry(recordId: Long) = viewModelScope.launch {
        withBusy {
            runCatching { retryFailedConsignment(recordId) }
                .onSuccess { ok ->
                    sendMessage(
                        if (ok) "Queued again" else "Nothing to retry",
                    )
                }
                .onFailure { sendMessage(it.message ?: it.toString()) }
        }
    }

    private suspend fun withBusy(block: suspend () -> Unit) {
        _isBusy.value = true
        try {
            block()
        } finally {
            _isBusy.value = false
        }
    }

    private suspend fun sendMessage(text: String) {
        _events.send(ConsignmentUiEvent.Message(text))
    }

    override fun onCleared() {
        _events.close()
        super.onCleared()
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ConsignmentViewModel::class.java)) {
                    return ConsignmentViewModel(
                        observeConsignments = container.observeConsignments,
                        intakeConsignments = container.intakeConsignments,
                        enqueueValidated = container.enqueueValidated,
                        processSyncQueue = container.processSyncQueue,
                        retryFailedConsignment = container.retryFailedConsignment,
                        clearLocalData = container.clearLocalData,
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel: $modelClass")
            }
        }
    }
}
