package com.example.guitar.repository

import com.example.guitar.entity.NoteEvent

interface NoteEventRepo {
    suspend fun addNoteEvent(noteEvent: NoteEvent)
    suspend fun getAllNoteEvents(): List<NoteEvent>
    suspend fun getNoteEventByRecordId(recordId: Int): List<NoteEvent>
}