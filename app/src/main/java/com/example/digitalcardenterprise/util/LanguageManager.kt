package com.example.digitalcardenterprise.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {
    enum class Lang(val code: String, val display: String) {
        RU("ru", "Русский"),
        EN("en", "English"),
        ZH("zh", "中文"),
        JA("ja", "日本語"),
        KO("ko", "한국어")
    }

    fun getSaved(context: Context): Lang {
        val code = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE).getString("language_code", null)
        return Lang.entries.firstOrNull { it.code == code } ?: defaultLang()
    }

    fun apply(context: Context, lang: Lang) {
        context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("language_code", lang.code)
            .apply()
        val locale = Locale(lang.code)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun recreate(activity: Activity, lang: Lang) {
        apply(activity, lang)
        activity.recreate()
    }

    private fun defaultLang(): Lang {
        val system = Locale.getDefault().language
        return when {
            system.startsWith("ru") -> Lang.RU
            system.startsWith("zh") -> Lang.ZH
            system.startsWith("ja") -> Lang.JA
            system.startsWith("ko") -> Lang.KO
            else -> Lang.EN
        }
    }
}
