package com.guitarsimulator.guitar.view.playingguitar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val chords = listOf("G", "Em", "Am", "F", "C")

@Composable
fun ChordPicker(modifier: Modifier = Modifier, onChordSelected: (String) -> Unit = {}) {
    var selectedChord by remember { mutableIntStateOf(-1) }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        chords.forEach { chord ->
            ChordPickerItem(
                name = chord,
                selected = selectedChord == chords.indexOf(chord),
                onClick = {
                    selectedChord = if (selectedChord == chords.indexOf(chord)) -1
                    else chords.indexOf(chord)
                    if (selectedChord != -1) onChordSelected(chord) else onChordSelected("")
                }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ChordPickerItem(
    modifier: Modifier = Modifier,
    name: String = "A",
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = Color.Black, shape = CircleShape)
            .clip(CircleShape)
            .background(if (selected) Color(0xFFFFBF51) else Color.White)
            .size(40.dp)
            .clickable(onClick = { onClick() }),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, color = Color.Black)
    }
}