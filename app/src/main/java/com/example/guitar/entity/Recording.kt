package com.example.guitar.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recording")
data class Recording(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val duration: Long,
    val timestamp: Long = System.currentTimeMillis()
)