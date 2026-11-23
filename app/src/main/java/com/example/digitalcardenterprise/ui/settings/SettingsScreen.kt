package com.example.digitalcardenterprise.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R
import com.example.digitalcardenterprise.ui.theme.LocalLanguage
import com.example.digitalcardenterprise.ui.theme.LocalThemeMode
import com.example.digitalcardenterprise.util.LanguageManager
import com.example.digitalcardenterprise.util.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentLang by remember { mutableStateOf(LanguageManager.getSaved(context)) }
    var currentTheme by remember { mutableStateOf(ThemeManager.getSaved(context)) }
    var showLangDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ListItem(headlineContent = { Text(stringResource(R.string.settings_language)) }, supportingContent = { Text(currentLang.display) }, modifier = Modifier.clickable { showLangDialog = true })
            HorizontalDivider()
            ListItem(headlineContent = { Text(stringResource(R.string.settings_theme)) }, supportingContent = { Text(currentTheme.name) }, modifier = Modifier.clickable { showThemeDialog = true })
            HorizontalDivider()
        }
    }

    if (showLangDialog) AlertDialog(
        onDismissRequest = { showLangDialog = false },
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column {
                LanguageManager.Lang.entries.forEach { lang ->
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            currentLang = lang
                            LanguageManager.apply(context, lang)
                            LocalLanguage.current = lang
                            showLangDialog = false
                        }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lang.display, Modifier.weight(1f))
                        if (lang == currentLang) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { showLangDialog = false }) { Text(stringResource(R.string.btn_cancel)) } }
    )

    if (showThemeDialog) AlertDialog(
        onDismissRequest = { showThemeDialog = false },
        title = { Text(stringResource(R.string.settings_theme)) },
        text = {
            Column {
                ThemeManager.Mode.entries.forEach { mode ->
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            currentTheme = mode
                            ThemeManager.apply(context, mode)
                            LocalThemeMode.current = mode
                            showThemeDialog = false
                        }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(mode.name, Modifier.weight(1f))
                        if (mode == currentTheme) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { showThemeDialog = false }) { Text(stringResource(R.string.btn_cancel)) } }
    )
}
