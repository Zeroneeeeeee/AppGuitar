package com.example.guitar.ui.playingguitar

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.math.sin
import com.example.guitar.R
import com.example.guitar.entity.NoteEvent
import com.example.guitar.entity.Recording
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI

data class RecordingVM(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val durationMs: Long,
    val sequence: List<NoteEventVM>
)

data class NoteEventVM(
    val baseNote: String,
    val fret: Int,
    val timestamp: Long
)

fun NoteEvent.toNoteEventVM() = NoteEventVM(
    baseNote = this.note,
    fret = this.fret,
    timestamp = this.id
)

fun NoteEventVM.toNoteEvent(recordingId: Int) = NoteEvent(
    note = this.baseNote,
    fret = this.fret,
    id = this.timestamp,
    recordingId = recordingId
)

fun Recording.toRecordingVM(sequence: List<NoteEventVM>) = RecordingVM(
    name = this.name,
    durationMs = this.duration,
    sequence = sequence,
    id = this.timestamp
)

fun RecordingVM.toRecording() = Recording(
    name = this.name,
    duration = this.durationMs,
    timestamp = this.id
)

fun Long.formatTimestamp(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatSecondsToMMSS(): String {
    val minutes = this / 60000
    val secs = (this/1000) % 60
    return String.format("%02d:%02d", minutes, secs)
}

fun String.baseNoteToString():Int{
    return when(this){
        "E2" -> 0
        "A2" -> 1
        "D3" -> 2
        "G3" -> 3
        "B3" -> 4
        "E4" -> 5
        else -> -1
    }
}

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