package com.example.guitar

import android.content.Context
import androidx.core.content.edit

object SharedPreference {
    private const val PREF_NAME = "MyPref"

    fun saveLanguage(context: Context, language: String) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit { putString("language", language) }
    }

    fun getLanguage(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString("language", null)
    }
}