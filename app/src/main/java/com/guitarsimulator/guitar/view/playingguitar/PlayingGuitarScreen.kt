package com.guitarsimulator.guitar.view.playingguitar

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.guitarsimulator.guitar.R
import com.guitarsimulator.guitar.dtmodels.NoteEventVM
import com.guitarsimulator.guitar.utils.Metronome
import com.guitarsimulator.guitar.view.playingguitar.component.GuitarFingerBoard
import kotlinx.coroutines.delay

@Composable
fun PlayingGuitarScreen(
    modifier: Modifier = Modifier,
    window: Window,
    localizedContext: Context,
    isTutorial: Boolean = false,
    isPlayback: Boolean = false,
    listNotes: List<NoteEventVM> = emptyList(),
    toTunerClick: () -> Unit = {},
    onBack: () -> Unit = {},
    toPlaylist: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isMetronomeOn by remember { mutableStateOf(false) }
    val metronome = remember { Metronome(context) }

    var isBackClicked by remember{mutableStateOf(true)}
    LaunchedEffect(isBackClicked) {
        if (!isBackClicked) {
            delay(2000)
            isBackClicked = true
        }
    }

    LaunchedEffect(Unit) {
        Log.d("LaunchedEffect", "PlayingGuitarScreen")
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        metronome.loadSound()
    }

    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isMetronomeOn) {
            metronome.start()
        } else {
            metronome.stop()
        }
        Image(
            painter = painterResource(R.drawable.img_guitar_background),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        GuitarFingerBoard(
            listNotes = listNotes,
            onBack = {
                if(isBackClicked){
                    isBackClicked = false
                    isMetronomeOn = false
                    onBack()
                }
            },
            onTunerClick = {
                toTunerClick()
            },
            toPlaylist = {
                isMetronomeOn = false
                toPlaylist()
            },
            isTutorial = isTutorial,
            isPlayback = isPlayback,
            isMetronomeOn = isMetronomeOn,
            onMetronomeClick = {
                isMetronomeOn = it
            },
            window = window,
            localizedContext = localizedContext,
            modifier = modifier
        )
    }
}


