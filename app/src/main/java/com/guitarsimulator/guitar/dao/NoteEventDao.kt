package com.guitarsimulator.guitar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.guitarsimulator.guitar.entity.NoteEvent

@Dao
interface NoteEventDao {
    @Insert
    suspend fun addNoteEvent(noteEvent: NoteEvent)
    @Query("SELECT * FROM note_event")
    suspend fun getAllNoteEvents(): List<NoteEvent>
    @Query("SELECT * FROM note_event WHERE recordingId = :recordingId")
    suspend fun getNoteEventById(recordingId: Int): List<NoteEvent>
}