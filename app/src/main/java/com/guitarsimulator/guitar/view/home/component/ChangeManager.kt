package com.guitarsimulator.guitar.view.home.component

import android.content.Context
import android.content.SharedPreferences

object ChanceManager {

    private const val PREF_NAME = "chance_prefs"
    private const val KEY_CHANCE = "chance_value"
    private const val INCREASE_STEP = 0.1f

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getChance(): Float {
        return prefs?.getFloat(KEY_CHANCE, 0f) ?: 0f
    }

    private fun setChance(value: Float) {
        prefs?.edit()?.putFloat(KEY_CHANCE, value)?.apply()
    }

    private fun resetChance() {
        setChance(0f)
    }

    fun handleClick(): Boolean {
        var chance = getChance()
        val randomValue = Math.random().toFloat()

        return if (randomValue < chance) {
            resetChance()
            true
        } else {
            chance = (chance + INCREASE_STEP).coerceAtMost(1f)
            setChance(chance)
            false
        }
    }
}
