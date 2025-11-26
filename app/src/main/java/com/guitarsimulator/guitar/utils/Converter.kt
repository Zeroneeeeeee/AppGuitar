package com.guitarsimulator.guitar.utils

import com.guitarsimulator.guitar.dtmodels.NoteEventVM
import com.guitarsimulator.guitar.dtmodels.RecordingVM
import com.guitarsimulator.guitar.entity.NoteEvent
import com.guitarsimulator.guitar.entity.Recording
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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