package com.example.guitar.repository

import com.example.guitar.entity.Recording

interface RecordingRepo {
    suspend fun insertRecording(recording: Recording): Int
    suspend fun getAllRecordings(): List<Recording>
    suspend fun getRecordingById(id: Int): Recording
    suspend fun updateRecording(name: String, timestamp: Long)
    suspend fun deleteRecording( timestamp: Long)
}