package com.example.digitalcardenterprise.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object ThemeManager {
    private const val PREFS = "theme_prefs"
    private const val KEY = "theme_mode"

    enum class Mode(val name: String) {
        LIGHT("light"),
        DARK("dark"),
        SYSTEM("system")
    }

    fun getSaved(context: Context): Mode {
        val code = context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null)
        return Mode.values().firstOrNull { it.name == code } ?: Mode.SYSTEM
    }

    fun apply(context: Context, mode: Mode) {
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, mode.name)
            .apply()
    }

    fun recreate(activity: Activity, mode: Mode) {
        apply(activity, mode)
        activity.recreate()
    }
}
