package com.example.guitar.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.guitar.entity.Recording

@Dao
interface RecordingDao {
    @Insert
    suspend fun insertRecording(recording: Recording):Long
    @Query("SELECT * FROM recording ORDER BY timestamp DESC")
    suspend fun getAllRecordings(): List<Recording>
    @Query("SELECT * FROM recording WHERE id = :id")
    suspend fun getRecordingById(id: Int): Recording
    @Query("UPDATE recording SET name =:name WHERE timestamp=:timestamp")
    suspend fun updateRecording(name: String, timestamp: Long)
    @Query("DELETE FROM recording WHERE timestamp =:timestamp")
    suspend fun deleteRecording(timestamp: Long)
}