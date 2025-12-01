package com.guitarsimulator.guitar.view.recordplaylist.component

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
import com.guitarsimulator.guitar.R
import com.guitarsimulator.guitar.utils.DemoMusic
import com.guitarsimulator.guitar.viewmodel.GuitarViewModel
import com.guitarsimulator.guitar.dtmodels.NoteEventVM
import com.guitarsimulator.guitar.dtmodels.RecordingVM

@Composable
fun Content(
    modifier: Modifier = Modifier,
    viewModel: GuitarViewModel = viewModel(),
    selectedTab: String = localizedContext.resources.getString(R.string.guitar_record),
    isTabVisible: Boolean = true,
    onRenameClick: (RecordingVM) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onPlayback: (List<NoteEventVM>) -> Unit,
    toTutorial: (List<NoteEventVM>) -> Unit,
    getSelectedTab: (String) -> Unit,
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
                        getSelectedTab(it)
                    }
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            }
            RecordingList(
                recordings = recordings,
                isPlaying = isPlaying,
                onPlayClick = { recording ->
//                    if (isPlaying) viewModel.stopPlayback()
//                    else viewModel.startPlayback(recording.sequence)
                    onPlayback(recording.sequence)
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