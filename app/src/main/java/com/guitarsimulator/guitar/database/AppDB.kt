package com.guitarsimulator.guitar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.guitarsimulator.guitar.dao.NoteEventDao
import com.guitarsimulator.guitar.dao.RecordingDao
import com.guitarsimulator.guitar.entity.NoteEvent
import com.guitarsimulator.guitar.entity.Recording

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