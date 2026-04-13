package com.bafanam.logistics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Input
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bafanam.logistics.R
import com.bafanam.logistics.domain.model.ConsignmentListRow
import com.bafanam.logistics.domain.model.ConsignmentRecordStatus
import com.bafanam.logistics.domain.model.SyncQueueOutcome
import com.bafanam.logistics.domain.model.ValidationState

@Composable
fun ConsignmentRoute(viewModel: ConsignmentViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ConsignmentUiEvent.Message -> snackbarHostState.showSnackbar(event.text)
            }
        }
    }

    ConsignmentScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntake = viewModel::runIntake,
        onEnqueue = viewModel::runEnqueue,
        onSync = viewModel::runSync,
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsignmentScreen(
    uiState: ConsignmentUiState,
    snackbarHostState: SnackbarHostState,
    onIntake: () -> Unit,
    onEnqueue: () -> Unit,
    onSync: () -> Unit,
    onRetry: (Long) -> Unit,
) {
    val guide = remember(uiState.rows) { dispatchGuide(uiState.rows) }
    val activeFlowStep = remember(guide) { flowStepHighlight(guide) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.screen_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            stringResource(R.string.screen_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    if (uiState.isBusy) {
                        LogisticsLoadingIndicator(
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    NextStepCard(guide = guide)
                }
                item {
                    WorkflowCard(
                        isBusy = uiState.isBusy,
                        activeFlowStep = activeFlowStep,
                        onIntake = onIntake,
                        onEnqueue = onEnqueue,
                        onSync = onSync,
                    )
                }
                item {
                    OverviewCard(rows = uiState.rows)
                }
                item {
                    Text(
                        if (uiState.rows.isEmpty()) {
                            stringResource(R.string.section_deliveries)
                        } else {
                            stringResource(R.string.section_deliveries_count, uiState.rows.size)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                if (uiState.rows.isEmpty()) {
                    item {
                        EmptyStateCard()
                    }
                }
                items(uiState.rows, key = { it.record.id }) { row ->
                    ShipmentCard(
                        row = row,
                        actionsEnabled = !uiState.isBusy,
                        onRetry = { onRetry(row.record.id) },
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
            if (uiState.isBusy) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LogisticsLoadingIndicator(
                    modifier = Modifier.scale(1.35f),
                )
                Text(
                    stringResource(R.string.loading_overlay_message),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

private enum class DispatchGuide {
    IMPORT,
    QUEUE,
    SYNC,
    CAUGHT_UP,
}

private fun dispatchGuide(rows: List<ConsignmentListRow>): DispatchGuide = when {
    rows.isEmpty() -> DispatchGuide.IMPORT
    rows.any { it.record.status == ConsignmentRecordStatus.VALIDATED } -> DispatchGuide.QUEUE
    rows.any { it.record.status == ConsignmentRecordStatus.QUEUED } -> DispatchGuide.SYNC
    else -> DispatchGuide.CAUGHT_UP
}

/** 0 = step 1 highlighted … 2 = step 3; null = all steps done style */
private fun flowStepHighlight(guide: DispatchGuide): Int? = when (guide) {
    DispatchGuide.IMPORT -> 0
    DispatchGuide.QUEUE -> 1
    DispatchGuide.SYNC -> 2
    DispatchGuide.CAUGHT_UP -> null
}

@Composable
private fun NextStepCard(guide: DispatchGuide) {
    val (titleRes, detailRes) = when (guide) {
        DispatchGuide.IMPORT -> R.string.guide_import_title to R.string.guide_import_detail
        DispatchGuide.QUEUE -> R.string.guide_queue_title to R.string.guide_queue_detail
        DispatchGuide.SYNC -> R.string.guide_sync_title to R.string.guide_sync_detail
        DispatchGuide.CAUGHT_UP -> R.string.guide_caught_up_title to R.string.guide_caught_up_detail
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                stringResource(R.string.section_next_step),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
            )
            Text(
                stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                stringResource(detailRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.92f),
            )
        }
    }
}

@Composable
private fun ThreeStepMiniFlow(activeStep: Int?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FlowStepPill(
            step = 1,
            label = stringResource(R.string.flow_step_1),
            state = flowStepState(0, activeStep),
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Outlined.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(16.dp),
            tint = MaterialTheme.colorScheme.outline,
        )
        FlowStepPill(
            step = 2,
            label = stringResource(R.string.flow_step_2),
            state = flowStepState(1, activeStep),
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Outlined.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(16.dp),
            tint = MaterialTheme.colorScheme.outline,
        )
        FlowStepPill(
            step = 3,
            label = stringResource(R.string.flow_step_3),
            state = flowStepState(2, activeStep),
            modifier = Modifier.weight(1f),
        )
    }
}

private fun flowStepState(index: Int, activeStep: Int?): FlowStepState = when {
    activeStep == null -> FlowStepState.Done
    index < activeStep -> FlowStepState.Done
    index == activeStep -> FlowStepState.Current
    else -> FlowStepState.Upcoming
}

private enum class FlowStepState { Current, Done, Upcoming }

@Composable
private fun FlowStepPill(
    step: Int,
    label: String,
    state: FlowStepState,
    modifier: Modifier = Modifier,
) {
    val (bg, fg) = when (state) {
        FlowStepState.Current ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        FlowStepState.Done ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        FlowStepState.Upcoming ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Surface(
            shape = CircleShape,
            color = bg,
            modifier = Modifier.size(32.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (state == FlowStepState.Done) {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = fg,
                    )
                } else {
                    Text(
                        step.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = fg,
                    )
                }
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OverviewCard(rows: List<ConsignmentListRow>) {
    val total = rows.size
    val validated = rows.count { it.record.status == ConsignmentRecordStatus.VALIDATED }
    val queued = rows.count { it.record.status == ConsignmentRecordStatus.QUEUED }
    val synced = rows.count { it.record.status == ConsignmentRecordStatus.SYNCED }
    val invalid = rows.count { it.record.status == ConsignmentRecordStatus.INVALID }
    val failed = rows.count { it.record.status == ConsignmentRecordStatus.FAILED }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                stringResource(R.string.section_overview),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        stringResource(R.string.overview_total_label),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Text(
                    total.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatBlock(
                    label = stringResource(R.string.overview_ready),
                    value = validated.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
                StatBlock(
                    label = stringResource(R.string.overview_queued),
                    value = queued.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatBlock(
                    label = stringResource(R.string.overview_synced),
                    value = synced.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
                StatBlock(
                    label = stringResource(R.string.overview_invalid),
                    value = invalid.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatBlock(
                    label = stringResource(R.string.overview_failed),
                    value = failed.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = color.copy(alpha = 0.85f))
    }
}

@Composable
private fun WorkflowCard(
    isBusy: Boolean,
    activeFlowStep: Int?,
    onIntake: () -> Unit,
    onEnqueue: () -> Unit,
    onSync: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(R.string.section_dispatch),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            ThreeStepMiniFlow(activeStep = activeFlowStep)
            Text(
                stringResource(R.string.workflow_intro),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            WorkflowStepButton(
                step = 1,
                title = stringResource(R.string.workflow_step1),
                subtitle = stringResource(R.string.workflow_step1_subtitle),
                icon = Icons.Outlined.Input,
                enabled = !isBusy,
                onClick = onIntake,
            )
            WorkflowStepButton(
                step = 2,
                title = stringResource(R.string.workflow_step2),
                subtitle = stringResource(R.string.workflow_step2_subtitle),
                icon = Icons.Outlined.LocalShipping,
                enabled = !isBusy,
                onClick = onEnqueue,
            )
            WorkflowStepButton(
                step = 3,
                title = stringResource(R.string.workflow_step3),
                subtitle = stringResource(R.string.workflow_step3_subtitle),
                icon = Icons.Outlined.CloudUpload,
                enabled = !isBusy,
                onClick = onSync,
            )
        }
    }
}

@Composable
private fun WorkflowStepButton(
    step: Int,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        step.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(
            Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Outlined.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.outline,
            )
            Text(
                stringResource(R.string.empty_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.empty_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ShipmentCard(
    row: ConsignmentListRow,
    actionsEnabled: Boolean,
    onRetry: () -> Unit,
) {
    val payload = parseShipmentPayloadDisplay(row.record.rawPayload)
    val style = statusStyle(row.record.status)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = statusIcon(row.record.status),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.label_order_id),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            row.record.businessKey,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                StatusBadge(style)
            }
            payload?.let { p ->
                LabeledRow(stringResource(R.string.label_customer), p.customerCode)
                LabeledRow(stringResource(R.string.label_items), p.itemCountLine)
                p.upstreamStatus?.let { LabeledRow(stringResource(R.string.label_order_status), it) }
            }
            LabeledRow(
                label = stringResource(R.string.label_checks),
                value = stringResource(validationSummaryRes(row.record.validationState)),
                valueColor = when (row.record.validationState) {
                    ValidationState.INVALID -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )
            row.record.failureReason?.let { reason ->
                Text(
                    reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            row.queue?.let { q ->
                LabeledRow(
                    label = stringResource(R.string.label_route),
                    value = stringResource(
                        R.string.queue_line,
                        queueOutcomeLabel(q.outcome),
                        q.attemptCount,
                    ),
                )
            }
            if (row.record.retryCount > 0) {
                Text(
                    stringResource(R.string.retry_count_line, row.record.retryCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (row.record.status == ConsignmentRecordStatus.FAILED) {
                OutlinedButton(onClick = onRetry, enabled = actionsEnabled, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.retry_sync))
                }
            }
        }
    }
}

@Composable
private fun LabeledRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(108.dp),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

private fun validationSummaryRes(state: ValidationState): Int = when (state) {
    ValidationState.VALID -> R.string.validation_ok
    ValidationState.INVALID -> R.string.validation_failed
    ValidationState.PENDING -> R.string.validation_pending
}

private fun statusIcon(status: ConsignmentRecordStatus): androidx.compose.ui.graphics.vector.ImageVector = when (status) {
    ConsignmentRecordStatus.NEW -> Icons.Outlined.Schedule
    ConsignmentRecordStatus.VALIDATED -> Icons.Outlined.PlaylistAdd
    ConsignmentRecordStatus.INVALID -> Icons.Outlined.ErrorOutline
    ConsignmentRecordStatus.QUEUED -> Icons.Outlined.LocalShipping
    ConsignmentRecordStatus.SYNCED -> Icons.Outlined.CheckCircle
    ConsignmentRecordStatus.FAILED -> Icons.Outlined.ErrorOutline
}

@Composable
private fun statusStyle(status: ConsignmentRecordStatus): StatusStyle {
    val s = MaterialTheme.colorScheme
    return when (status) {
        ConsignmentRecordStatus.NEW -> StatusStyle(
            stringResource(R.string.status_new),
            s.surfaceVariant,
            s.onSurfaceVariant,
        )
        ConsignmentRecordStatus.VALIDATED -> StatusStyle(
            stringResource(R.string.status_validated),
            s.primaryContainer,
            s.onPrimaryContainer,
        )
        ConsignmentRecordStatus.INVALID -> StatusStyle(
            stringResource(R.string.status_invalid),
            s.errorContainer,
            s.onErrorContainer,
        )
        ConsignmentRecordStatus.QUEUED -> StatusStyle(
            stringResource(R.string.status_queued),
            s.secondaryContainer,
            s.onSecondaryContainer,
        )
        ConsignmentRecordStatus.SYNCED -> StatusStyle(
            stringResource(R.string.status_synced),
            s.tertiaryContainer,
            s.onTertiaryContainer,
        )
        ConsignmentRecordStatus.FAILED -> StatusStyle(
            stringResource(R.string.status_failed),
            s.errorContainer,
            s.onErrorContainer,
        )
    }
}

private data class StatusStyle(
    val label: String,
    val container: androidx.compose.ui.graphics.Color,
    val content: androidx.compose.ui.graphics.Color,
)

@Composable
private fun StatusBadge(style: StatusStyle) {
    Surface(
        shape = RoundedCornerShape(50),
        color = style.container,
    ) {
        Text(
            style.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = style.content,
        )
    }
}

@Composable
private fun queueOutcomeLabel(outcome: SyncQueueOutcome): String = when (outcome) {
    SyncQueueOutcome.PENDING -> stringResource(R.string.queue_outcome_waiting)
    SyncQueueOutcome.SUCCESS -> stringResource(R.string.queue_outcome_delivered)
    SyncQueueOutcome.FAILED -> stringResource(R.string.queue_outcome_error)
}
