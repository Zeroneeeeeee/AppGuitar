package com.guitarsimulator.guitar.dtmodels

data class RecordingVM(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val durationMs: Long,
    val sequence: List<NoteEventVM>
)