package com.bafanam.logistics.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bafanam.logistics.R

/**
 * A small “shipment on route” animation: truck moves along a lane between depot-style markers.
 */
@Composable
fun LogisticsLoadingIndicator(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logistics_route")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "truck_along_route",
    )

    val routeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    val markerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f)
    val truckTint = MaterialTheme.colorScheme.primary

    BoxWithConstraints(
        modifier = modifier
            .width(76.dp)
            .height(34.dp)
            .padding(end = 12.dp),
    ) {
        val truckSize = 24.dp
        val travel = (maxWidth - truckSize).coerceAtLeast(0.dp)
        val truckX = travel * progress

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(5.dp)
                .padding(horizontal = 2.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(routeColor),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = 2.dp, y = (-1).dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(markerColor),
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-2).dp, y = (-1).dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(markerColor),
        )
        Icon(
            imageVector = Icons.Outlined.LocalShipping,
            contentDescription = stringResource(R.string.loading_content_description),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = truckX, y = (-3).dp)
                .size(truckSize),
            tint = truckTint,
        )
    }
}
