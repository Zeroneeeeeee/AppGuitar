package com.example.guitar.view.playingguitar

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.guitar.R

@Composable
fun CustomGridScrollBar(
    scrollState: ScrollState,
    itemWidth: Dp,
    totalItems: Int,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val itemWidthPx = with(density) { itemWidth.toPx() }

    var barWidth by remember { mutableFloatStateOf(1f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }

    val thumbFraction by remember {
        derivedStateOf {
            val totalWidthPx = totalItems * itemWidthPx
            (barWidth / totalWidthPx).coerceIn(0f, 1f)
        }
    }

    val scrollFraction by remember {
        derivedStateOf {
            if (scrollState.maxValue == 0) 0f
            else scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        }
    }

    LaunchedEffect(scrollFraction) {
        Log.d("ScrollFraction", "ScrollFraction: $scrollFraction")
    }

    Box(
        modifier = modifier
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .onSizeChanged { barWidth = it.width.toFloat() }
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                for (i in 1..21) {
                    val x = size.width / 22 * i
                    drawLine(
                        color = Color(0xFFAAAAAA),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = strokeWidth
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val scrollableWidth = barWidth * (1f - thumbFraction)
                        Log.d("ThumbFraction", "ThumbFraction: $thumbFraction")
                        if (scrollableWidth > 0) {
                            val fractionDelta = dragAmount.x / scrollableWidth
                            dragFraction = (dragFraction + fractionDelta).coerceIn(0f, 1f)
                        }
                    }
                )
            }
    ) {
        val displayFraction = if (isDragging) dragFraction else scrollFraction
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(thumbFraction)
                .graphicsLayer {
                    translationX = displayFraction * (barWidth - barWidth * thumbFraction)
                }
            //.border(1.dp,Color.Black)
            ,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pitch),
                contentDescription = "Pitch",
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxHeight(),
            )
        }
    }

    LaunchedEffect(dragFraction, isDragging) {
        if (isDragging) {
            scrollState.scrollTo((dragFraction * scrollState.maxValue).toInt())
        }
    }
}