package com.guitarsimulator.guitar.view.playingguitar.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.guitarsimulator.guitar.R
import kotlin.math.roundToInt

@Composable
fun GuitarString(modifier: Modifier = Modifier, string: Int = -1, trigger: Long = 0L) {
    val triggers = remember { mutableStateListOf(0, 0, 0, 0, 0, 0) }

    LaunchedEffect(trigger) {
        if (string in 0..5) {
            triggers[string]++
        }
    }

    Column(modifier = modifier) {
        for (i in 0..5) {
            Spacer(Modifier.weight(0.5f))
            VibrationString(
                trigger = triggers[i],
                stringImg = if (i in 0..3) R.drawable.ic_bass_string else R.drawable.ic_main_string
            )
            Spacer(Modifier.weight(0.5f))
        }
    }
}

@Composable
fun VibrationString(modifier: Modifier = Modifier, trigger: Int = 0, stringImg: Int) {
    val pxToMove = with(LocalDensity.current) { 3.dp.toPx() }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger > 0) {
            var ampli = pxToMove
            repeat(35) {
                offsetY.animateTo(ampli, animationSpec = tween(1))
                offsetY.animateTo(-ampli, animationSpec = tween(1))
                ampli *= 0.9f
            }
            offsetY.animateTo(0f, animationSpec = tween(1))
        }
    }

    Image(
        painter = painterResource(stringImg),
        contentDescription = "String",
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .background(color = Color.Yellow)
    )
}