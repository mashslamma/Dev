package com.example.digitalcardenterprise.ui.theme

import androidx.compose.runtime.CompositionLocal
import com.example.digitalcardenterprise.util.LanguageManager.Lang
import com.example.digitalcardenterprise.util.ThemeManager.Mode

object LocalLanguage {
    var current by CompositionLocal.StaticProvider(Lang.EN)
}

object LocalThemeMode {
    var current by CompositionLocal.StaticProvider(Mode.SYSTEM)
}
