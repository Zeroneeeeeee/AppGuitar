package com.example.guitar.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.math.sin
import com.example.guitar.R
import kotlin.math.PI

data class NoteSampleData(
    val audioData: ShortArray,
    val sampleRate: Int
)

val baseStringNotes = mapOf(
    "E2" to R.raw.string_e2,
    "A2" to R.raw.string_a2,
    "D3" to R.raw.string_d3,
    "G3" to R.raw.string_g3,
    "B3" to R.raw.string_b3,
    "E4" to R.raw.string_e4
)

class SoundManager(context: Context) {

    private val soundMap = mutableMapOf<String, Int>()
    private val sampleDataMap = mutableMapOf<String, NoteSampleData>()
    private val soundPool: SoundPool

    val handSlapSoundId: Int

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(12)
            .setAudioAttributes(attributes)
            .build()

        handSlapSoundId = soundPool.load(context, R.raw.hand_slap_sound, 1)

        baseStringNotes.forEach { (noteId, resId) ->
            val soundId = soundPool.load(context, resId, 1)
            soundMap[noteId] = soundId

            val sampleRate = 44100
            val durationSeconds = 0.5
            val totalSamples = (sampleRate * durationSeconds).toInt()

            val baseFrequency = when(noteId) {
                "E2" -> 82.41; "A2" -> 110.0; "D3" -> 146.8
                "G3" -> 196.0; "B3" -> 246.9; "E4" -> 329.6
                else -> 440.0
            }

            val dummySamples = ShortArray(totalSamples) { i ->
                val amplitude = 5000
                (amplitude * sin(2 * PI * baseFrequency * i / sampleRate)).toInt().toShort()
            }
            sampleDataMap[noteId] = NoteSampleData(dummySamples, sampleRate)
        }
    }

    fun playNote(baseNote: String, rate: Float) {
        val soundId = soundMap[baseNote]
        if (soundId != null) {
            soundPool.play(soundId, 1f, 1f, 1, 0, rate)
        }
    }

    fun playHandSlap() {
        soundPool.play(handSlapSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun getSampleData(noteId: String): NoteSampleData? {
        return sampleDataMap[noteId]
    }

    fun release() {
        soundPool.release()
        soundMap.clear()
        sampleDataMap.clear()
    }
}