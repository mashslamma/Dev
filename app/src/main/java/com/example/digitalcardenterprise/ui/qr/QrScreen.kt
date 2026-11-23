package com.example.digitalcardenterprise.ui.qr

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R
import com.example.digitalcardenterprise.data.UserProfile
import com.example.digitalcardenterprise.util.ImageHelper
import com.example.digitalcardenterprise.util.PdfExporter
import com.example.digitalcardenterprise.util.QrCache
import com.example.digitalcardenterprise.util.QrGenerator
import com.example.digitalcardenterprise.util.VCardGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScreen(profile: UserProfile, onBack: () -> Unit) {
    val context = LocalContext.current
    val vcard = remember(profile) { VCardGenerator.generate(profile) }
    val cacheKey = remember { "${profile.uid}_${vcard.hashCode()}" }

    val qrBitmap = remember(cacheKey) {
        QrCache.get(cacheKey)?.let { cached ->
            cached
        } ?: run {
            val generated = QrGenerator.generate(vcard)
            generated?.let { QrCache.put(cacheKey, it) }
            generated
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.qr_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${profile.firstName} ${profile.lastName}".trim(), style = MaterialTheme.typography.headlineSmall)
            if (profile.position.isNotBlank() || profile.company.isNotBlank()) Text(text = listOf(profile.position, profile.company).filter { it.isNotBlank() }.joinToString(" • "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            if (qrBitmap != null) {
                Card { Image(qrBitmap.asImageBitmap(), null, Modifier.size(300.dp).padding(12.dp)) }
                Spacer(Modifier.height(16.dp))
                Text(text = stringResource(R.string.qr_hint), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(28.dp))
                Button(onClick = { val uri = ImageHelper.saveBitmapToCache(context, qrBitmap) ?: return@Button; val intent = Intent(Intent.ACTION_SEND).apply { type = "image/png"; putExtra(Intent.EXTRA_STREAM, uri); putExtra(Intent.EXTRA_TEXT, "Digital Business Card"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }; context.startActivity(Intent.createChooser(intent, context.getString(R.string.qr_share))) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.qr_share))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { val uri = PdfExporter.export(context, profile, qrBitmap) ?: return@OutlinedButton; val intent = Intent(Intent.ACTION_SEND).apply { type = "application/pdf"; putExtra(Intent.EXTRA_STREAM, uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }; context.startActivity(Intent.createChooser(intent, context.getString(R.string.qr_pdf))) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.qr_pdf)) }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { val uri = ImageHelper.saveTextToCache(context, vcard, "contact_${profile.uid}.vcf") ?: return@OutlinedButton; val intent = Intent(Intent.ACTION_SEND).apply { type = "text/vcard"; putExtra(Intent.EXTRA_STREAM, uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }; context.startActivity(Intent.createChooser(intent, context.getString(R.string.qr_vcf))) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.qr_vcf)) }
            } else {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) { Text(text = stringResource(R.string.qr_error), modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center) }
            }
        }
    }
}
