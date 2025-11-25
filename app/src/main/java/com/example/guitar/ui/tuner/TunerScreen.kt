//package com.example.guitar.ui.tuner
//
//import android.Manifest
//import android.media.AudioFormat
//import android.media.AudioRecord
//import android.media.MediaRecorder
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.annotation.RequiresPermission
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import kotlin.math.abs
//
//@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
//@RequiresPermission(Manifest.permission.RECORD_AUDIO)
//@Composable
//fun GuitarTunerScreen() {
//    var detectedFreq by remember { mutableStateOf<Float?>(null) }
//    var closestString by remember { mutableStateOf("---") }
//    var diffHz by remember { mutableStateOf<Float?>(null) }
//    var isActive by remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        startGuitarTuner(
//            onFreqDetected = { freq ->
//                if (freq > 0) {
//                    val (name, target) = closestGuitarString(freq)
//                    closestString = name
//                    detectedFreq = freq
//                    diffHz = freq - target
//                    isActive = true
//                } else {
//                    isActive = false
//                }
//            }
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = closestString,
//            color = Color.White,
//            style = MaterialTheme.typography.displayLarge
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        FrequencyCircleGraph(
//            diffHz = diffHz ?: 0f,
//            isActive = isActive,
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .height(400.dp)
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        if (isActive && detectedFreq != null) {
//            Text(
//                text = "${"%.2f".format(detectedFreq!!)} Hz",
//                color = Color.LightGray
//            )
//        } else {
//            Text(
//                text = "🎤 Đang chờ âm thanh...",
//                color = Color.Gray
//            )
//        }
//    }
//}
//
//@Composable
//fun FrequencyCircleGraph(
//    diffHz: Float,
//    isActive: Boolean,
//    modifier: Modifier = Modifier,
//    rangeHz: Float = 10f
//) {
//    val circleRadius = 20f
//    val trailPoints = remember { mutableStateListOf<Offset>() }
//    var screenOffset by remember { mutableStateOf(0f) }
//
//    LaunchedEffect(isActive, diffHz) {
//        while (true) {
//            delay(16L)
//            screenOffset -= 2f
//
//            val clamped = diffHz.coerceIn(-rangeHz, rangeHz)
//            val percent = (clamped / rangeHz) * 0.5f + 0.5f
//            val circleX = 50f + percent * 700f
//
//            val circleY = 700f
//            trailPoints.add(Offset(circleX, circleY - screenOffset))
//
//            if (trailPoints.size > 800) trailPoints.removeAt(0)
//        }
//    }
//
//    Canvas(modifier = modifier) {
//        val width = size.width
//        val height = size.height
//
//        val gridYStep = 50f
//        for (y in 0..(height / gridYStep).toInt()) {
//            drawLine(
//                color = Color.DarkGray.copy(alpha = 0.25f),
//                start = Offset(0f, y * gridYStep),
//                end = Offset(width, y * gridYStep),
//                strokeWidth = 1f
//            )
//        }
//
//        drawLine(
//            color = Color.Gray,
//            start = Offset(width / 2, 0f),
//            end = Offset(width / 2, height),
//            strokeWidth = 2f
//        )
//
//        for (point in trailPoints) {
//            drawCircle(
//                color = Color.Red,
//                radius = 4f,
//                center = Offset(point.x, point.y + screenOffset)
//            )
//
//        }
//
//        val clamped = diffHz.coerceIn(-rangeHz, rangeHz)
//        val percent = (clamped / rangeHz) * 0.5f + 0.5f
//        val circleX = 50f + percent * 700f
//        val circleY = 700f
//        drawCircle(
//            color = Color.Blue,
//            radius = circleRadius,
//            center = Offset(circleX, circleY)
//        )
//    }
//}
//
//val guitarStrings = listOf(
//    "E2" to 82.41f,
//    "A2" to 110.00f,
//    "D3" to 146.83f,
//    "G3" to 196.00f,
//    "B3" to 246.94f,
//    "E4" to 329.63f
//)
//
//// Tìm dây gần nhất
//fun closestGuitarString(freq: Float): Pair<String, Float> {
//    if (freq <= 0) return "---" to 0f
//    return guitarStrings.minByOrNull { abs(it.second - freq) } ?: ("---" to 0f)
//}
//
//@RequiresPermission(Manifest.permission.RECORD_AUDIO)
//fun startGuitarTuner(
//    onFreqDetected: (Float) -> Unit,
//    onWaveData: (List<Float>) -> Unit = {}
//): Job {
//    val sampleRate = 44100
//    val bufferSize = AudioRecord.getMinBufferSize(
//        sampleRate,
//        AudioFormat.CHANNEL_IN_MONO,
//        AudioFormat.ENCODING_PCM_16BIT
//    )
//    val recorder = AudioRecord(
//        MediaRecorder.AudioSource.MIC,
//        sampleRate,
//        AudioFormat.CHANNEL_IN_MONO,
//        AudioFormat.ENCODING_PCM_16BIT,
//        bufferSize
//    )
//    recorder.startRecording()
//
//    return CoroutineScope(Dispatchers.Default).launch {
//        val buffer = ShortArray(bufferSize)
//        val waveform = ArrayDeque<Float>()
//        val maxPoints = 800
//
//        while (isActive) {
//            val read = recorder.read(buffer, 0, buffer.size)
//            if (read > 0) {
//                // normalize
//                val samples = buffer.take(read).map { it / 32768f }
//                samples.forEach {
//                    waveform.addLast(it)
//                    if (waveform.size > maxPoints) waveform.removeFirst()
//                }
//                onWaveData(waveform.toList())
//
//                val freq = detectPitch(buffer, sampleRate)
//                if (freq in 60f..500f) {
//                    withContext(Dispatchers.Main) {
//                        onFreqDetected(freq)
//                    }
//                }
//            }
//        }
//        recorder.stop()
//        recorder.release()
//    }
//}
//
//fun detectPitch(buffer: ShortArray, sampleRate: Int): Float {
//    val samples = buffer.map { it.toFloat() }.toFloatArray()
//    val mean = samples.average().toFloat()
//    for (i in samples.indices) samples[i] -= mean
//
//    var bestOffset = -1
//    var bestCorr = 0f
//    val corr = FloatArray(1000)
//
//    for (offset in 20 until 1000) {
//        var c = 0f
//        for (i in 0 until samples.size - offset) {
//            c += samples[i] * samples[i + offset]
//        }
//        corr[offset] = c
//        if (c > bestCorr) {
//            bestCorr = c
//            bestOffset = offset
//        }
//    }
//
//    if (bestOffset in 21 until 999) {
//        val y1 = corr[bestOffset - 1]
//        val y2 = corr[bestOffset]
//        val y3 = corr[bestOffset + 1]
//        val shift = 0.5f * (y1 - y3) / (y1 - 2 * y2 + y3)
//        return sampleRate.toFloat() / (bestOffset + shift)
//    }
//
//    return if (bestOffset != -1) sampleRate.toFloat() / bestOffset else 0f
//}
