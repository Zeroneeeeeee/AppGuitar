package com.example.guitar.dtmodels

data class NoteEventVM(
    val baseNote: String,
    val fret: Int,
    val timestamp: Long
)