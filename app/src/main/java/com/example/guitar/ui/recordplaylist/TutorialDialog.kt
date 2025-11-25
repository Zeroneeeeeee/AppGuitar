package com.example.guitar.ui.recordplaylist

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guitar.R

@Composable
fun TutorialDialog(onDismiss: () -> Unit = {}, localizedContext: Context) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Content(
            onDismiss = onDismiss, modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {}
            ),
            localizedContext = localizedContext
        )
    }
}

@Composable
fun Content(modifier: Modifier = Modifier, onDismiss: () -> Unit = {}, localizedContext: Context) {
    val recordings = listOf(
        Tutorial(R.drawable.img_recordings_tutor_1,
            localizedContext.getString(R.string.click_on_a_recording_to_play_it)
        ),
        Tutorial(R.drawable.img_recordings_tutor_2,
            localizedContext.getString(R.string.click_on_the_play_button_to_start_tutorial)
        ),
        Tutorial(
            R.drawable.img_recordings_tutor_3,
            localizedContext.getString(R.string.click_on_options_icon_to_rename_or_delete_the_recording)
        ),
    )
    var index by remember { mutableIntStateOf(0) }
    Column(
        modifier = modifier
            .width(300.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Showcase(modifier = Modifier.fillMaxWidth(), tutorial = recordings[index])
        Spacer(modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    index = (index - 1).coerceAtLeast(0)
                },
                enabled = index > 0
            ) {
                Text(text = localizedContext.getString(R.string.previous))
            }
            Button(
                onClick = {
                    if (index == recordings.size - 1) onDismiss()
                    index = (index + 1).coerceAtMost(recordings.size - 1)
                }
            ) {
                Text(
                    text = if (index == recordings.size - 1) localizedContext.getString(R.string.done) else localizedContext.getString(
                        R.string.next
                    )
                )
            }
        }
    }
}

data class Tutorial(
    val image: Int,
    val description: String
)

@Composable
fun Showcase(
    modifier: Modifier = Modifier,
    tutorial: Tutorial = Tutorial(
        image = R.drawable.img_recordings,
        description = "Click on a recording to play it"
    )
) {
    Column(
        modifier = modifier
            .width(270.dp)
    ) {
        Image(
            painter = painterResource(tutorial.image),
            contentDescription = "Recordings",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier.height(16.dp))
        Text(text = tutorial.description, modifier = Modifier.fillMaxWidth())
    }
}
