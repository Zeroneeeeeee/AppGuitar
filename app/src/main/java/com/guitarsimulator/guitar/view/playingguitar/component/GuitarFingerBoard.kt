package com.guitarsimulator.guitar.view.playingguitar.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.Window
import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guitarsimulator.guitar.R
import com.guitarsimulator.guitar.dtmodels.NoteEventVM
import com.guitarsimulator.guitar.utils.baseNoteToString
import com.guitarsimulator.guitar.viewmodel.GuitarViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun GuitarFingerBoard(
    modifier: Modifier = Modifier,
    isMetronomeOn: Boolean = false,
    isTutorial: Boolean = false,
    listNotes: List<NoteEventVM> = emptyList(),
    window: Window,
    onMetronomeClick: (Boolean) -> Unit = {},
    onTunerClick: () -> Unit = {},
    onBack: () -> Unit = {},
    toPlaylist: () -> Unit = {},
    viewModel: GuitarViewModel = viewModel(),
    localizedContext: Context
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.fetchRecordings()
        if (!viewModel.isSoundManagerInitialized) {
            viewModel.initialize(context)
        }
    }

    var isMirrored by remember { mutableStateOf(false) }
    val isRecording by viewModel.isRecording.collectAsState()
    var playingString by remember { mutableIntStateOf(-1) }
    var duration by remember { mutableLongStateOf(0L) }
    var trigger by remember { mutableLongStateOf(0L) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    var isShowIndex by remember { mutableStateOf(false) }

    var time by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            time = System.currentTimeMillis() - startTime
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isRecording) {
                                viewModel.stopRecording(getDuration = { duration = it })
                                isSaveDialogOpen = true
                            }
                            if (!isSaveDialogOpen) {
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) Color.Red else Color.White,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .width(102.dp)
                            .height(48.dp),
                        onClick = {
                            if (isRecording) {
                                viewModel.stopRecording(getDuration = { duration = it })
                                isRunning = false
                                if (viewModel.recordedSequence.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        localizedContext.getString(R.string.no_notes_detected_during_recording),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    isSaveDialogOpen = true
                                }
                                time = 0
                            } else {
                                startTime = System.currentTimeMillis() - time
                                isRunning = true
                                viewModel.startRecording()
                            }
                        },
                    ) {
                        if (!isRecording) {
                            Icon(
                                painter = painterResource(R.drawable.ic_record),
                                contentDescription = "Record",
                                tint = Color.Unspecified,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(text = formatTime(timeMi = time), color = Color.White)
                        }
                    }
                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = { toPlaylist() },
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, Color.White, CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_play_arrow),
                            contentDescription = "Start/Stop Playback",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = { isDialogOpen = true },
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, Color.White, CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tap),
                            contentDescription = "Tap",
                            tint = Color.Unspecified
                        )
                    }
                    Spacer(Modifier.width(8.dp))

                    CustomGridScrollBar(
                        scrollState = scrollState,
                        totalItems = 22,
                        itemWidth = 100.dp,
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .align(Alignment.CenterVertically),
                    )
                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            //onTunerClick()
                            Toast.makeText(
                                context,
                                context.getString(R.string.coming_soon), Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, Color.White, CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tuner),
                            contentDescription = "Tuner",
                            tint = Color.Unspecified
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp)
                        .graphicsLayer(if (isMirrored) -1f else 1f)
                ) {
                    val density = LocalDensity.current
                    var imageHeightPx by remember { mutableFloatStateOf(0f) }
                    val columnHeightDp = with(density) { (imageHeightPx * 0.82f).toDp() }
                    var imageWidthPx by remember { mutableFloatStateOf(0f) }
                    val columnWidthDp = with(density) { (imageWidthPx).toDp() }
                    val listFret = remember { mutableStateListOf<Int>() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)

                    ) {
                        Image(
                            painter = painterResource(R.drawable.img_head),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxHeight()
                                .onGloballyPositioned { layoutCoordinates ->
                                    imageHeightPx = layoutCoordinates.size.height.toFloat()
                                    imageWidthPx = layoutCoordinates.size.width.toFloat()
                                }
                        )

                        Image(
                            painter = painterResource(R.drawable.img_guitar_fingerboard),
                            contentDescription = "",
                            contentScale = ContentScale.None,
                            modifier = Modifier
                                .width(2200.dp)
                                .height(columnHeightDp)
                                .align(Alignment.CenterVertically)
                        )
                    }

                    GuitarHorizontalGrid(
                        scrollState = scrollState,
                        localizeContext = localizedContext,
                        getPlayingString = {
                            playingString = it
                            trigger = System.currentTimeMillis()
                        },
                        listNote = listNotes,
                        columnWidthDp = columnWidthDp,
                        columnHeightDp = columnHeightDp,
                        listFret = listFret,
                        showIndex = isShowIndex,
                        isMirrored = isMirrored,
                        onNotePlayed = viewModel::onGuitarFretClicked,
                        modifier = Modifier.align(Alignment.Center),
                        isTutorial = isTutorial
                    )

                    GuitarString(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(columnHeightDp)
                            .align(Alignment.Center),
                        string = playingString,
                        trigger = trigger
                    )

                    ChordPicker(
                        onChordSelected = {
                            when (it) {
                                "G" -> {
                                    listFret.clear(); listFret.addAll(listOf(3, 2, 0, 0, 0, 3))
                                }

                                "Em" -> {
                                    listFret.clear(); listFret.addAll(listOf(0, 2, 2, 0, 0, 0))
                                }

                                "Am" -> {
                                    listFret.clear(); listFret.addAll(listOf(0, 0, 2, 2, 1, 0))
                                }

                                "F" -> {
                                    listFret.clear(); listFret.addAll(listOf(1, 3, 3, 2, 1, 1))
                                }

                                "C" -> {
                                    listFret.clear(); listFret.addAll(listOf(0, 3, 2, 0, 1, 0))
                                }

                                "" -> {
                                    listFret.clear()
                                }
                            }
                        },
                        modifier = Modifier
                            .height(columnHeightDp)
                            .background(
                                color = Color(0x33FFFAEB),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    bottomStart = 10.dp
                                )
                            )
                            .graphicsLayer(if (isMirrored) -1f else 1f)
                            .align(Alignment.CenterEnd)
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = {}
                            )
                    )
                }
            }
        }
        if (isDialogOpen) {
            SettingDialog(
                hand = if (isMirrored) 1 else 0,
                onMetronome = isMetronomeOn,
                onShowIndex = isShowIndex,
                localizedContext = localizedContext,
                onMetronomeClick = onMetronomeClick,
                onDismiss = { isDialogOpen = false },
                onHandClick = { hand ->
                    isMirrored = hand == 1
                },
                onShowIndexClick = { isShowIndex = it }
            )
        }
        if (isSaveDialogOpen) {
            SaveDialog(
                onCancel = {
                    isSaveDialogOpen = false
                    Toast.makeText(
                        context,
                        localizedContext.getString(R.string.cancel_successfully), Toast.LENGTH_SHORT
                    ).show()
                },
                onSave = {
                    viewModel.saveRecording(it, duration)
                    isSaveDialogOpen = false
                    Toast.makeText(
                        context,
                        localizedContext.getString(R.string.record_saved_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                window = window,
                localizedContext = localizedContext
            )
        }
    }
}

@Composable
fun GuitarHorizontalGrid(
    modifier: Modifier = Modifier,
    localizeContext: Context,
    scrollState: ScrollState,
    columnHeightDp: Dp,
    columnWidthDp: Dp,
    isTutorial: Boolean = false,
    showIndex: Boolean = false,
    isMirrored: Boolean,
    listNote: List<NoteEventVM> = emptyList(),
    getPlayingString: (Int) -> Unit = {},
    listFret: List<Int> = listOf(-1, -1, -1, -1, -1, -1),
    onNotePlayed: (baseNote: String, fret: Int) -> Unit,
) {
    val stringNames = remember { listOf("E2", "A2", "D3", "G3", "B3", "E4") }
    val frets = 23
    val fretWidth = 100.dp
    var boxHeight by remember { mutableFloatStateOf(0f) }
    val playedNotes = remember { mutableSetOf<Pair<Int, Int>>() }

    var activeHighlightIndex by remember { mutableIntStateOf(0) }

    var draggingFret by remember { mutableIntStateOf(-1) }
    var draggingString by remember { mutableIntStateOf(-1) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    LaunchedEffect(activeHighlightIndex) {
        val activeNote = listNote.getOrNull(activeHighlightIndex)

        if (activeNote != null) {
            val fretNumber = activeNote.fret

            val targetIndex = (fretNumber - 1)
            val totalItems = frets

            if (targetIndex >= 0 && targetIndex < totalItems) {
                val offsetPx = with(density) { -fretWidth.toPx() }
                val targetPx = with(density) { targetIndex * fretWidth.toPx() + offsetPx }

                coroutineScope.launch {
                    delay(100)
                    scrollState.animateScrollTo(
                        value = targetPx.toInt()
                    )
                }
            }
        }

        if (isTutorial && listNote.getOrNull(activeHighlightIndex) == null) {
            Toast.makeText(
                context,
                localizeContext.getString(R.string.tutorial_complete), Toast.LENGTH_SHORT
            ).show()
        }
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(columnHeightDp)

    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .height((columnHeightDp))
                        .align(alignment = Alignment.Center)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                val stringHeightPx = columnHeightDp.toPx() / 6
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.first()
                                    val pos = change.position

                                    val fret = (pos.x / fretWidth.toPx()).toInt()

                                    val string = (pos.y / stringHeightPx).toInt().coerceIn(0, 5)

                                    val key = string to fret
                                    if (playedNotes.add(key)) {
                                        val baseNote = stringNames[string]
                                        val effectiveFret =
                                            if (listFret.isEmpty()) fret else listFret[string]
                                        onNotePlayed(baseNote, effectiveFret)
                                        getPlayingString(string)

                                        val activeNote = listNote.getOrNull(activeHighlightIndex)
                                        if (activeNote != null &&
                                            fret == activeNote.fret &&
                                            string == activeNote.baseNote.baseNoteToString()
                                        ) {
                                            activeHighlightIndex += 1
                                        }

                                        draggingFret = fret
                                        draggingString = string
                                    }

                                    if (change.changedToUp()) {
                                        playedNotes.clear()
                                        draggingFret = -1
                                        draggingString = -1
                                    }
                                }
                            }
                        }
                ) {
                    for (index in 0..5) {
                        val string = index % 6
                        val fret = 0
                        val activeNote = listNote.getOrNull(activeHighlightIndex)
                        val isActive = activeNote != null &&
                                fret == activeNote.fret &&
                                string == activeNote.baseNote.baseNoteToString()
                        Box(
                            modifier = Modifier
                                .width(columnWidthDp)
                                .weight(1f / 6f)
                                .drawBehind {
                                    val strokeWidth = 2.dp.toPx()
                                    val x = size.width - strokeWidth / 2
                                    drawLine(
                                        color = Color(0xFFAAAAAA),
                                        start = Offset(x, 0f),
                                        end = Offset(x, size.height),
                                        strokeWidth = strokeWidth
                                    )
                                }
                                .drawBehind {
                                    if (fret == draggingFret && string == draggingString || isActive) {
                                        drawRect(
                                            color = Color.Cyan.copy(alpha = 0.2f),
                                            size = Size(
                                                width = columnWidthDp.toPx(),
                                                height = boxHeight
                                            ),
                                            topLeft = Offset(0f, 0f)
                                        )
                                    }
                                },
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (showIndex) {
                                Text(
                                    text = stringNames[string],
                                    color = Color.Black,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .graphicsLayer(if (isMirrored) -1f else 1f)
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(columnHeightDp)
                    .horizontalScroll(scrollState, enabled = false)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            val stringHeightPx = columnHeightDp.toPx() / 6
                            val fretWidthPx = fretWidth.toPx()

                            val pointerState = mutableMapOf<PointerId, Pair<Int, Int>>()

                            while (true) {
                                val event = awaitPointerEvent()

                                for (change in event.changes) {

                                    val pos = change.position

                                    val string = (pos.y / stringHeightPx)
                                        .toInt()
                                        .coerceIn(0, 5)

                                    val fret = ((pos.x / fretWidthPx)
                                        .toInt()
                                        .coerceIn(0, frets - 2)) + 1

                                    val last = pointerState[change.id]
                                    val lastString = last?.first
                                    val lastFret = last?.second

                                    if (last == null || string != lastString || fret != lastFret) {

                                        val baseNote = stringNames[string]
                                        val effectiveFret =
                                            if (listFret.isEmpty()) fret else listFret[string]

                                        onNotePlayed(baseNote, effectiveFret)
                                        getPlayingString(string)

                                        val activeNote = listNote.getOrNull(activeHighlightIndex)
                                        if (activeNote != null &&
                                            fret == activeNote.fret &&
                                            string == activeNote.baseNote.baseNoteToString()
                                        ) {
                                            activeHighlightIndex += 1
                                        }

                                        draggingFret = fret
                                        draggingString = string

                                        pointerState[change.id] = string to fret
                                    }

                                    if (change.changedToUp()) {
                                        pointerState.remove(change.id)
                                        playedNotes.clear()
                                        draggingFret = -1
                                        draggingString = -1
                                    }
                                }
                            }
                        }
                    }
                    .align(alignment = Alignment.CenterVertically),
            ) {

                Row {
                    repeat(frets) {
                        val fret = it + 1
                        Box {
                            when (fret) {
                                3, 5, 7, 9, 15, 17, 19, 21 -> {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_dot),
                                        contentDescription = "Dot",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                }

                                12 -> {
                                    Column(
                                        modifier = Modifier
                                            .height(columnHeightDp / 2)
                                            .align(Alignment.Center),
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_dot),
                                            contentDescription = "Dot",
                                            tint = Color.Unspecified,
                                        )
                                        Spacer(modifier = Modifier.height(columnHeightDp / 6))
                                        Icon(
                                            painter = painterResource(R.drawable.ic_dot),
                                            contentDescription = "Dot",
                                            tint = Color.Unspecified,
                                        )
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .width(fretWidth)
                            ) {
                                repeat(6) { string ->
                                    val activeNote = listNote.getOrNull(activeHighlightIndex)
                                    val isActive = activeNote != null &&
                                            fret == activeNote.fret &&
                                            string == activeNote.baseNote.baseNoteToString()
                                    val isOn =
                                        fret == draggingFret && string == draggingString || isActive

                                    var alphaTarget by remember { mutableFloatStateOf(0f) }

                                    LaunchedEffect(isOn) {
                                        alphaTarget = if (isOn) {
                                            1f
                                        } else {
                                            0f
                                        }
                                    }

                                    val highlightAlpha by animateFloatAsState(
                                        targetValue = alphaTarget,
                                        animationSpec = tween(
                                            durationMillis = if (alphaTarget == 0f) 1000 else 0,
                                            easing = LinearOutSlowInEasing
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(fretWidth)
                                            .height(columnHeightDp / 6)
                                            .drawBehind {
                                                val strokeWidth = 2.dp.toPx()
                                                val x = size.width - strokeWidth / 2
                                                if (fret in 0..21) {
                                                    drawLine(
                                                        color = Color(0xFFAAAAAA),
                                                        start = Offset(x, 0f),
                                                        end = Offset(x, size.height),
                                                        strokeWidth = strokeWidth
                                                    )
                                                }
                                            }
                                            .onSizeChanged { it -> boxHeight = it.height.toFloat() }
                                            .clipToBounds()
                                            .drawBehind {
                                                if (highlightAlpha > 0f) {
                                                    drawRect(
                                                        color = Color.Cyan.copy(alpha = 0.25f * highlightAlpha),
                                                        size = Size(
                                                            width = fretWidth.toPx(),
                                                            height = boxHeight
                                                        ),
                                                        topLeft = Offset(0f, 0f)
                                                    )
                                                }
                                            },
                                        contentAlignment = Alignment.TopStart
                                    ) {
                                        if (showIndex) {
                                            if (string == 0) {
                                                Text(
                                                    text = if (fret in 0..22) fret.toString() else "",
                                                    color = Color.Gray,
                                                    modifier = Modifier
                                                        .padding(start = 8.dp)
                                                        .graphicsLayer(if (isMirrored) -1f else 1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun formatTime(timeMi: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMi)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMi) % 60
    return String.format("%02d:%02d", minutes, seconds)
}