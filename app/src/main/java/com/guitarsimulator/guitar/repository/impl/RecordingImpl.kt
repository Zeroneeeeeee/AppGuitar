package com.guitarsimulator.guitar.repository.impl

import android.content.Context
import com.guitarsimulator.guitar.database.AppDB
import com.guitarsimulator.guitar.entity.Recording
import com.guitarsimulator.guitar.repository.RecordingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecordingImpl(context: Context) : RecordingRepo {
    private val recordingDao = AppDB.getInstance(context).recordingDao()
    override suspend fun insertRecording(recording: Recording): Int {
        return withContext(Dispatchers.IO) {
            recordingDao.insertRecording(recording).toInt()
        }
    }

    override suspend fun getAllRecordings(): List<Recording> {
        return withContext(Dispatchers.IO) {
            recordingDao.getAllRecordings()
        }
    }

    override suspend fun getRecordingById(id: Int): Recording {
        return withContext(Dispatchers.IO) {
            recordingDao.getRecordingById(id)
        }
    }

    override suspend fun updateRecording(name: String, timestamp: Long) {
        withContext(Dispatchers.IO) {
            recordingDao.updateRecording(name, timestamp)
        }
    }

    override suspend fun deleteRecording(timestamp: Long) {
        withContext(Dispatchers.IO) {
            recordingDao.deleteRecording(timestamp)
        }
    }
}