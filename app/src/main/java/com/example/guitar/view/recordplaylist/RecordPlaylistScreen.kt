package com.example.guitar.view.recordplaylist

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guitar.R
import com.example.guitar.viewmodel.GuitarViewModel
import com.example.guitar.dtmodels.NoteEventVM
import com.example.guitar.view.recordplaylist.component.Content
import com.example.guitar.view.recordplaylist.component.DeleteDialog
import com.example.guitar.view.recordplaylist.component.PlaylistHeader
import com.example.guitar.view.recordplaylist.component.RenameDialog
import com.example.guitar.view.recordplaylist.component.TutorialDialog

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

    val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
    windowInsertController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

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
                    windowInsertController.show(WindowInsetsCompat.Type.systemBars())
                    viewModel.stopPlayback()
                    onBack()
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
                localizedContext = localizedContext
            )
        }
    }
}
