package com.example.guitar.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_event", foreignKeys = [
        ForeignKey(
            entity = Recording::class,
            parentColumns = ["id"],
            childColumns = ["recordingId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class NoteEvent(
    @PrimaryKey val id: Long= System.currentTimeMillis(),
    val note: String,
    val fret: Int,
    val recordingId: Int
)