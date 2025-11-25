package com.example.guitar.ui.recordplaylist.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guitar.R
import com.example.guitar.ui.playingguitar.DemoMusic
import com.example.guitar.ui.playingguitar.GuitarViewModel
import com.example.guitar.ui.playingguitar.NoteEventVM
import com.example.guitar.ui.playingguitar.RecordingVM

@Composable
fun Content(
    modifier: Modifier = Modifier,
    viewModel: GuitarViewModel = viewModel(),
    selectedTab: String = localizedContext.resources.getString(R.string.guitar_record),
    isTabVisible: Boolean = true,
    onRenameClick: (RecordingVM) -> Unit,
    onDeleteClick: (Long) -> Unit,
    toTutorial: (List<NoteEventVM>) -> Unit,
    localizedContext: Context,
    toGuitarScreen: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(selectedTab) }
    val isPlaying by viewModel.isPlaying.collectAsState()
    val recordings =
        if (selectedTab == localizedContext.resources.getString(R.string.guitar_record)) viewModel.recordings.collectAsState().value else DemoMusic.demoRecordings
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.fetchRecordings()
        if (!viewModel.isSoundManagerInitialized) {
            viewModel.initialize(context)
        }
    }
    LaunchedEffect(recordings) {
        Log.d("GuitarViewModel1", "Recordings updated: $recordings")
    }

    Box() {
        Row(modifier = modifier.fillMaxWidth()) {
            if (isTabVisible) {
                ChoiceTab(
                    localizedContext = localizedContext,
                    getSelectedTab = {
                        if (selectedTab != it) viewModel.stopPlayback()
                        selectedTab = it
                    }
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            }
            RecordingList(
                recordings = recordings,
                isPlaying = isPlaying,
                onPlayClick = { recording ->
                    if (isPlaying) viewModel.stopPlayback()
                    else viewModel.startPlayback(recording.sequence)
                },
                onExportClick = viewModel::exportRecording,
                onRenameClick = {
                    onRenameClick(it)
                },
                onDeleteClick = {
                    onDeleteClick(it)
                },
                toTutorial = toTutorial,
                localizedContext = localizedContext,
                type = selectedTab,
                onOpenGuitarClick = toGuitarScreen
            )
        }
    }
}