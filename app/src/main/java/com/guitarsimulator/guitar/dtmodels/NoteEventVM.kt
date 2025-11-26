package com.guitarsimulator.guitar.dtmodels

data class NoteEventVM(
    val baseNote: String,
    val fret: Int,
    val timestamp: Long
)