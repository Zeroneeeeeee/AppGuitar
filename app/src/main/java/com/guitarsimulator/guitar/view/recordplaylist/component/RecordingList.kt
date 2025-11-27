package com.guitarsimulator.guitar.view.recordplaylist.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.guitarsimulator.guitar.R
import com.guitarsimulator.guitar.dtmodels.NoteEventVM
import com.guitarsimulator.guitar.dtmodels.RecordingVM
import com.guitarsimulator.guitar.utils.formatSecondsToMMSS
import com.guitarsimulator.guitar.utils.formatTimestamp

@Composable
fun RecordingList(
    recordings: List<RecordingVM>,
    isPlaying: Boolean,
    localizedContext: Context,
    type: String,
    onOpenGuitarClick: () -> Unit,
    onPlayClick: (RecordingVM) -> Unit,
    onExportClick: (RecordingVM) -> Unit,
    onRenameClick: (RecordingVM) -> Unit,
    onDeleteClick: (Long) -> Unit,
    toTutorial: (List<NoteEventVM>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(150.dp)
            .padding(horizontal = 8.dp)
    ) {
        var chosenId by remember { mutableStateOf(0L) }
        if (recordings.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_empty),
                    contentDescription = "Empty",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(134.dp)
                )
                Text(
                    text = localizedContext.getString(R.string.you_don_t_have_any_keys_record_files_yet)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onOpenGuitarClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background,
                    )
                ) {
                    Text(text = localizedContext.getString(R.string.open_guitar))
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(recordings) { recording ->
                    RecordingItem(
                        recording = recording,
                        isPlaying = recording.id == chosenId && isPlaying,
                        onPlayClick = { id ->
                            onPlayClick(recording)
                            chosenId = id
                        },
                        onExportClick = { onExportClick(recording) },
                        onRenameClick = onRenameClick,
                        onDeleteClick = onDeleteClick,
                        localizedContext = localizedContext,
                        toTutorial = toTutorial,
                        type = type
                    )
                }
            }
        }
    }
}

data class Option(val id: Int, val name: String, val icon: Int)

@Composable
fun RecordingItem(
    recording: RecordingVM = RecordingVM(
        name = "Recording 1",
        sequence = emptyList(),
        durationMs = 1000
    ),
    isPlaying: Boolean = false,
    onPlayClick: (Long) -> Unit = {},
    toTutorial: (List<NoteEventVM>) -> Unit = {},
    onExportClick: () -> Unit = {},
    onRenameClick: (RecordingVM) -> Unit = {},
    onDeleteClick: (Long) -> Unit = {},
    type: String = "",
    localizedContext: Context
) {
    val options =
        listOf(
            Option(1, localizedContext.resources.getString(R.string.rename), R.drawable.ic_edit),
            Option(2, localizedContext.resources.getString(R.string.delete), R.drawable.ic_delete)
        )
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPlaying) Color(0xFFFFBF51).copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = {
                onPlayClick(recording.id)
            })
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thông tin
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recording.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = recording.id.formatTimestamp(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }

        Text(text = recording.durationMs.formatSecondsToMMSS())

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow),
            contentDescription = "Play/Stop",
            tint = Color.White,
            modifier = Modifier
                .size(32.dp)
                .background(Color.Black, CircleShape)
                .padding(4.dp)
                .clickable(onClick = { toTutorial(recording.sequence) })
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (type != localizedContext.resources.getString(R.string.tutorial)) {
            Box {
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = "More",
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(option.icon),
                                        contentDescription = option.name,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(option.name)
                                }
                            },
                            onClick = {
                                when (option.id) {
                                    1 -> {
                                        onRenameClick(recording)
                                    }

                                    2 -> {
                                        onDeleteClick(recording.id)
                                    }
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}