package com.example.digitalcardenterprise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.digitalcardenterprise.ui.navigation.AppNav
import com.example.digitalcardenterprise.ui.theme.LocalLanguage
import com.example.digitalcardenterprise.ui.theme.LocalThemeMode
import com.example.digitalcardenterprise.util.LanguageManager
import com.example.digitalcardenterprise.util.ThemeManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedLang = LanguageManager.getSaved(this)
        val savedTheme = ThemeManager.getSaved(this)

        super.onCreate(savedInstanceState)

        setContent {
            LocalLanguage.current = savedLang
            LocalThemeMode.current = savedTheme
            MaterialTheme {
                Surface {
                    AppNav()
                }
            }
        }
    }
}
