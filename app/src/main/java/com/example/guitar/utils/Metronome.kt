package com.example.guitar.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.guitar.R
import kotlinx.coroutines.*

class Metronome(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var soundId: Int = 0
    private var job: Job? = null
    private var isPlaying = false
    private val bpm = 120 // tốc độ cố định

    fun loadSound() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool!!.load(context, R.raw.metronome, 1)
    }

    fun start() {
        if (isPlaying) return
        isPlaying = true

        val intervalMs = 60000L / bpm

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isPlaying) {
                soundPool?.play(soundId, 1f, 1f, 0, 0, 1f)
                delay(intervalMs)
            }
        }
    }

    fun stop() {
        isPlaying = false
        job?.cancel()
    }

    fun release() {
        stop()
        soundPool?.release()
        soundPool = null
    }
}
