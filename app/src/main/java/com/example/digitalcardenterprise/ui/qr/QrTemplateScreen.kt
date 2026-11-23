package com.example.digitalcardenterprise.ui.qr

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R
import com.example.digitalcardenterprise.data.UserProfile
import com.example.digitalcardenterprise.util.QrCache
import com.example.digitalcardenterprise.util.VCardGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrTemplateScreen(
    profile: UserProfile,
    selectedTemplate: QrTemplateStyle,
    onTemplateChange: (QrTemplateStyle) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vcard = remember(profile) { VCardGenerator.generate(profile) }
    val cacheKey = remember { "${profile.uid}_${selectedTemplate}" }

    val qrBitmap = remember(cacheKey) {
        QrCache.get(cacheKey) ?: run {
            val generated = QrGeneratorWithTemplate.generateWithTemplate(vcard, selectedTemplate)
            generated?.let { QrCache.put(cacheKey, it) }
            generated
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Дизайн QR") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Выберите стиль QR", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QrTemplateButton(
                    QrTemplateStyle.CLASSIC,
                    "Классика",
                    selectedTemplate == QrTemplateStyle.CLASSIC,
                    { onTemplateChange(QrTemplateStyle.CLASSIC) }
                )
                QrTemplateButton(
                    QrTemplateStyle.ROUNDED,
                    "Закругл.",
                    selectedTemplate == QrTemplateStyle.ROUNDED,
                    { onTemplateChange(QrTemplateStyle.ROUNDED) }
                )
                QrTemplateButton(
                    QrTemplateStyle.GRADIENT,
                    "Градиент",
                    selectedTemplate == QrTemplateStyle.GRADIENT,
                    { onTemplateChange(QrTemplateStyle.GRADIENT) }
                )
                QrTemplateButton(
                    QrTemplateStyle.FRAMED,
                    "Рамка",
                    selectedTemplate == QrTemplateStyle.FRAMED,
                    { onTemplateChange(QrTemplateStyle.FRAMED) }
                )
            }

            Spacer(Modifier.height(24.dp))

            if (qrBitmap != null) {
                Card {
                    androidx.compose.foundation.Image(
                        qrBitmap.asImageBitmap(),
                        null,
                        Modifier.size(300.dp).padding(12.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.qr_hint), textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(32.dp))

            Button(onClick = { /* шарить */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Поделиться QR")
            }
        }
    }
}

@Composable
private fun QrTemplateButton(
    style: QrTemplateStyle,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(64.dp)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = if (selected) Color.White else Color.Black
            )
        }
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
