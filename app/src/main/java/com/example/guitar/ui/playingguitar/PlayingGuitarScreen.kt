package com.example.guitar.ui.playingguitar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guitar.R
import com.example.guitar.ui.recordplaylist.component.Keyboard
import com.example.guitar.ui.recordplaylist.component.TextFieldAboveKeyboard
import com.example.guitar.ui.recordplaylist.component.keyboardAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun PlayingGuitarScreen(
    modifier: Modifier = Modifier,
    window: Window,
    localizedContext: Context,
    isTutorial: Boolean = false,
    listNotes: List<NoteEventVM> = emptyList(),
    toTunerClick: () -> Unit = {},
    onBack: () -> Unit = {},
    toPlaylist: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isMetronomeOn by remember { mutableStateOf(false) }
    val metronome = remember { Metronome(context) }

    val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
    windowInsertController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    LaunchedEffect(Unit) {
        Log.d("LaunchedEffect", "PlayingGuitarScreen")
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        metronome.loadSound()
    }

    Row(
        modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isMetronomeOn) {
            metronome.start()
        } else {
            metronome.stop()
        }
        GuitarFingerBoard(
            listNotes = listNotes,
            onBack = {
                windowInsertController.show(WindowInsetsCompat.Type.systemBars())
                isMetronomeOn = false
                onBack()
            },
            onTunerClick = {
                toTunerClick()
            },
            toPlaylist = {
                isMetronomeOn = false
                toPlaylist()
            },
            isTutorial = isTutorial,
            isMetronomeOn = isMetronomeOn,
            onMetronomeClick = {
                isMetronomeOn = it
            },
            window = window,
            localizedContext = localizedContext
        )
    }
}

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
    localizedContext:Context
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
                                isSaveDialogOpen = true
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
                            Text(text = formatTime(timeMi = time))
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
                            .clip(RoundedCornerShape(8.dp))
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
                                    Log.d("Loaded", "Loaded")
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
                        isTutorial = isTutorial,
                        onBack = onBack
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
                onShowIndexClick ={isShowIndex = it}
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
                        localizedContext.getString(R.string.record_saved_successfully), Toast.LENGTH_SHORT
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
    showIndex:Boolean = false,
    isMirrored:Boolean,
    listNote: List<NoteEventVM> = emptyList(),
    getPlayingString: (Int) -> Unit = {},
    listFret: List<Int> = listOf(-1, -1, -1, -1, -1, -1),
    onNotePlayed: (baseNote: String, fret: Int) -> Unit,
    onBack: () -> Unit = {}
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
            delay(2000)
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
                            if(showIndex) {
                                Text(
                                    text = stringNames[string],
                                    color = Color.Black,
                                    modifier = Modifier.padding(end = 8.dp).graphicsLayer(if (isMirrored) -1f else 1f)
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
                                        .coerceIn(0, frets - 1)) + 1


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
                                    Box(
                                        modifier = Modifier
                                            .width(fretWidth)
                                            .height(columnHeightDp / 6)
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
                                            .onSizeChanged { it -> boxHeight = it.height.toFloat() }
                                            .clipToBounds()
                                            .drawBehind {
                                                if (fret == draggingFret && string == draggingString || isActive) {
                                                    drawRect(
                                                        color = Color.Cyan.copy(alpha = 0.2f),
                                                        size = Size(
                                                            width = fretWidth.toPx(),
                                                            height = boxHeight
                                                        ),
                                                        topLeft = Offset(0f, 0f)
                                                    )
                                                }
                                            },
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        if(showIndex) {
                                            if (string == 0) {
                                                Text(
                                                    text = fret.toString(),
                                                    color = Color.Gray,
                                                    modifier = Modifier.padding(end = 8.dp).graphicsLayer(if (isMirrored) -1f else 1f)
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

@Composable
fun CustomGridScrollBar(
    scrollState: ScrollState,
    itemWidth: Dp,
    totalItems: Int,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val itemWidthPx = with(density) { itemWidth.toPx() }

    var barWidth by remember { mutableFloatStateOf(1f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }

    val thumbFraction by remember {
        derivedStateOf {
            val totalWidthPx = totalItems * itemWidthPx
            (barWidth / totalWidthPx).coerceIn(0f, 1f)
        }
    }

    val scrollFraction by remember {
        derivedStateOf {
            if (scrollState.maxValue == 0) 0f
            else scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        }
    }

    LaunchedEffect(scrollFraction) {
        Log.d("ScrollFraction", "ScrollFraction: $scrollFraction")
    }

    Box(
        modifier = modifier
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .onSizeChanged { barWidth = it.width.toFloat() }
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                for (i in 1..21) {
                    val x = size.width / 22 * i
                    drawLine(
                        color = Color(0xFFAAAAAA),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = strokeWidth
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val scrollableWidth = barWidth * (1f - thumbFraction)
                        Log.d("ThumbFraction", "ThumbFraction: $thumbFraction")
                        if (scrollableWidth > 0) {
                            val fractionDelta = dragAmount.x / scrollableWidth
                            dragFraction = (dragFraction + fractionDelta).coerceIn(0f, 1f)
                        }
                    }
                )
            }
    ) {
        val displayFraction = if (isDragging) dragFraction else scrollFraction
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(thumbFraction)
                .graphicsLayer {
                    translationX = displayFraction * (barWidth - barWidth * thumbFraction)
                }
            //.border(1.dp,Color.Black)
            ,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pitch),
                contentDescription = "Pitch",
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxHeight(),
            )
        }
    }

    LaunchedEffect(dragFraction, isDragging) {
        if (isDragging) {
            scrollState.scrollTo((dragFraction * scrollState.maxValue).toInt())
        }
    }
}

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SaveDialog(
    modifier: Modifier = Modifier,
    window: Window,
    localizedContext: Context,
    onCancel: () -> Unit = {},
    onSave: (String) -> Unit = {}
) {
    val isImeVisible by keyboardAsState()
    var name by remember { mutableStateOf("") }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { }
    ) {
        Column(
            modifier = modifier
                .width(300.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                )
                .border(4.dp, Color.Black, RoundedCornerShape(15.dp))
                .padding(8.dp)
                .background(Color(0xFFFFBF51), RoundedCornerShape(15.dp))
                .drawBehind {
                    val strokeWidth = 8.dp.toPx()

                    drawLine(
                        color = Color(0xFFF8D69B),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = Color(0xFFF8D69B),
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = Color(0xFFDB6E0E),
                        start = Offset(size.width, -8f),
                        end = Offset(size.width, size.height + 8f),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = Color(0xFFDB6E0E),
                        start = Offset(-8f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = localizedContext.getString(R.string.save_record),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = localizedContext.getString(R.string.do_you_want_to_save_this_record),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xffF8D69B),
                    unfocusedContainerColor = Color(0xffF8D69B),
                    focusedIndicatorColor = Color(0xFFDB6E0E),
                    unfocusedIndicatorColor = Color(0xFFDB6E0E),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),

                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onCancel() },
                    colors = ButtonDefaults.buttonColors(Color(0xFFDB6E0E)),
                ) {
                    Text(text = localizedContext.getString(R.string.cancel), color = Color.White)
                }

                Button(
                    onClick = { onSave(name) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDB6E0E),
                        disabledContentColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    enabled = name.isNotEmpty()
                ) {
                    Text(text = localizedContext.getString(R.string.save))
                }
            }
        }
    }
    if (isImeVisible == Keyboard.Opened) {
        val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsertController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        TextFieldAboveKeyboard(name = name, onValueChange = { name = it })
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun formatTime(timeMi: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMi)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMi) % 60
    return String.format("%02d:%02d", minutes, seconds)
}