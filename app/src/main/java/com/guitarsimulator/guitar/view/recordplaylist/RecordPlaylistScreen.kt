package com.guitarsimulator.guitar.view.recordplaylist

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guitarsimulator.guitar.R
import com.guitarsimulator.guitar.viewmodel.GuitarViewModel
import com.guitarsimulator.guitar.dtmodels.NoteEventVM
import com.guitarsimulator.guitar.view.recordplaylist.component.Content
import com.guitarsimulator.guitar.view.recordplaylist.component.DeleteDialog
import com.guitarsimulator.guitar.view.recordplaylist.component.PlaylistHeader
import com.guitarsimulator.guitar.view.recordplaylist.component.RenameDialog
import com.guitarsimulator.guitar.view.recordplaylist.component.Tutorial
import com.guitarsimulator.guitar.view.recordplaylist.component.TutorialDialog
import kotlinx.coroutines.delay

@Composable
fun RecordPlaylistScreen(
    modifier: Modifier = Modifier,
    viewModel: GuitarViewModel = viewModel(),
    onBack: () -> Unit = {},
    toTutorial: (List<NoteEventVM>) -> Unit = {},
    isTabVisible: Boolean = true,
    selectedTab: String = localizedContext.resources.getString(R.string.guitar_record),
    localizedContext: Context,
    window: Window,
    toGuitarScreen: () -> Unit
) {
    var exposeRename by remember { mutableStateOf(false) }
    var chosenId by remember { mutableStateOf(0L) }
    var previousName by remember { mutableStateOf("") }
    var exposeDelete by remember { mutableStateOf(false) }
    var exposeGuideline by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    var isBackClicked by remember{mutableStateOf(true)}
    LaunchedEffect(isBackClicked) {
        if (!isBackClicked) {
            delay(2000)
            isBackClicked = true
        }
    }

    LaunchedEffect(Unit) {
        Log.d("RecordPlaylistScreen",selectedTab)
    }

    val recordingTutorials = listOf(
        Tutorial(
            R.drawable.img_recordings_tutor_1,
            localizedContext.getString(R.string.click_on_a_recording_to_play_it)
        ),
        Tutorial(
            R.drawable.img_recordings_tutor_3,
            localizedContext.getString(R.string.click_on_options_icon_to_rename_or_delete_the_recording)
        ),
    )
    val learningTutorials = listOf(
        Tutorial(
            R.drawable.img_recordings_tutor_1,
            localizedContext.getString(R.string.click_on_a_recording_to_play_it)
        ),
        Tutorial(R.drawable.img_recordings_tutor_2,
            localizedContext.getString(R.string.click_on_the_play_button_to_start_tutorial)
        ),
    )

    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            PlaylistHeader(
                isGuideline = true,
                onBackClick = {
                    if(isBackClicked){
                        isBackClicked = false
                        viewModel.stopPlayback()
                        onBack()
                    }
                },
                onExposeTutorial = { exposeGuideline = true },
                title = if (isTabVisible) localizedContext.resources.getString(R.string.record_playlist) else selectedTab
            )
            Content(
                viewModel = viewModel,
                onRenameClick = { recording ->
                    exposeRename = true
                    chosenId = recording.id
                    previousName = recording.name
                },
                onDeleteClick = { id ->
                    exposeDelete = true
                    chosenId = id
                },
                toTutorial = {
                    viewModel.stopPlayback()
                    toTutorial(it)
                },
                localizedContext = localizedContext,
                isTabVisible = isTabVisible,
                selectedTab = selectedTab,
                toGuitarScreen = {
                    viewModel.stopPlayback()
                    toGuitarScreen()
                }
            )
        }

        if (exposeRename) {
            RenameDialog(
                name = previousName,
                onDismiss = { exposeRename = false },
                onSaveClick = { newName ->
                    viewModel.renameRecord(newName = newName, timestamp = chosenId)
                    exposeRename = false
                    Toast.makeText(
                        context,
                        localizedContext.getString(R.string.rename_complete), Toast.LENGTH_SHORT
                    ).show()
                },
                localizedContext = localizedContext,
                window = window
            )
        }
        if (exposeDelete) {
            DeleteDialog(
                onDismiss = { exposeDelete = false },
                onDeleteClick = {
                    viewModel.deleteRecord(timestamp = chosenId)
                    exposeDelete = false
                    Toast.makeText(
                        context,
                        localizedContext.getString(R.string.delete_complete), Toast.LENGTH_SHORT
                    ).show()
                },
                localizedContext = localizedContext
            )
        }
        if (exposeGuideline) {
            TutorialDialog(
                onDismiss = { exposeGuideline = false },
                localizedContext = localizedContext,
                tutorials = if(selectedTab == localizedContext.resources.getString(R.string.guitar_record)) recordingTutorials else learningTutorials
            )
        }
    }
}
