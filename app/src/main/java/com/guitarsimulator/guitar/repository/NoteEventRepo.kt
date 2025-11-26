package com.guitarsimulator.guitar.repository

import com.guitarsimulator.guitar.entity.NoteEvent

interface NoteEventRepo {
    suspend fun addNoteEvent(noteEvent: NoteEvent)
    suspend fun getAllNoteEvents(): List<NoteEvent>
    suspend fun getNoteEventByRecordId(recordId: Int): List<NoteEvent>
}