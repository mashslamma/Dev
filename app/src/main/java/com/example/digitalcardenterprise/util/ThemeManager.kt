package com.example.digitalcardenterprise.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFS = "theme_prefs"; private const val KEY = "theme_mode"
    enum class Mode(val value: Int) {
        LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
        DARK(AppCompatDelegate.MODE_NIGHT_YES),
        SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    fun getSaved(context: Context): Mode {
        val code = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
        return Mode.values().firstOrNull { it.value.toString() == code } ?: Mode.SYSTEM
    }
    fun apply(context: Context, mode: Mode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, mode.value.toString()).apply()
        AppCompatDelegate.setDefaultNightMode(mode.value)
    }
    fun recreate(activity: Activity, mode: Mode) { apply(activity, mode); activity.recreate() }
}
