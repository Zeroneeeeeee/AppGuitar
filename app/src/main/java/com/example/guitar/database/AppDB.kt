package com.example.guitar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.guitar.dao.NoteEventDao
import com.example.guitar.dao.RecordingDao
import com.example.guitar.entity.NoteEvent
import com.example.guitar.entity.Recording
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NoteEvent::class, Recording::class], version = 1)
abstract class AppDB: RoomDatabase() {
    abstract fun noteEventDao(): NoteEventDao
    abstract fun recordingDao(): RecordingDao
    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null
        fun getInstance(context: Context): AppDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java, "money.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }

    }
}