package com.guitarsimulator.guitar.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object PreferencesKeys {
    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
}

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    val onboardingDone = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.ONBOARDING_DONE] ?: false
    }

    suspend fun setOnboardingDone() {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.ONBOARDING_DONE] = true
        }
    }
}

