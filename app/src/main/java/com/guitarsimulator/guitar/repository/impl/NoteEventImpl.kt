package com.guitarsimulator.guitar.repository.impl

import android.content.Context
import com.guitarsimulator.guitar.database.AppDB
import com.guitarsimulator.guitar.entity.NoteEvent
import com.guitarsimulator.guitar.repository.NoteEventRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteEventImpl(context: Context) : NoteEventRepo {
    val noteEventDao = AppDB.getInstance(context).noteEventDao()
    override suspend fun addNoteEvent(noteEvent: NoteEvent) {
        withContext(Dispatchers.IO) {
            noteEventDao.addNoteEvent(noteEvent)
        }
    }

    override suspend fun getAllNoteEvents(): List<NoteEvent> {
        return withContext(Dispatchers.IO) {
            noteEventDao.getAllNoteEvents()
        }
    }

    override suspend fun getNoteEventByRecordId(recordId: Int): List<NoteEvent> {
        return withContext(Dispatchers.IO) {
            noteEventDao.getNoteEventById(recordId)
        }
    }
}