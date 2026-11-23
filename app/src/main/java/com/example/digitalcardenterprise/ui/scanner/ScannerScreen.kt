package com.example.digitalcardenterprise.ui.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.digitalcardenterprise.R
import com.example.digitalcardenterprise.util.AnalyticsManager
import com.example.digitalcardenterprise.util.ImageHelper
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(onBack: () -> Unit, onScanned: (String) -> Unit = {}) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> hasPermission = granted }
    LaunchedEffect(Unit) { if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA) }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.scanner_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        if (hasPermission) ScannerCamera(modifier = Modifier.fillMaxSize().padding(padding), onScanned = { text -> AnalyticsManager.logScan("vcard", "", "view", text); onScanned(text) })
        else Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.scanner_permission)); Spacer(Modifier.height(16.dp))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) { Text(stringResource(R.string.scanner_grant)) }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable private fun ScannerCamera(modifier: Modifier = Modifier, onScanned: (String) -> Unit) {
    val context = LocalContext.current; val lifecycleOwner = LocalLifecycleOwner.current
    var scanned by remember { mutableStateOf<String?>(null) }; var locked by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        AndroidView(factory = { ctx ->
            val pv = PreviewView(ctx); val exec = Executors.newSingleThreadExecutor(); val scanner = BarcodeScanning.getClient()
            ProcessCameraProvider.getInstance(ctx).addListener({
                val provider = ProcessCameraProvider.getInstance(ctx).get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(pv.surfaceProvider) }
                val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                analysis.setAnalyzer(exec) { proxy ->
                    if (locked) { proxy.close(); return@setAnalyzer }
                    proxy.image?.let { m ->
                        scanner.process(InputImage.fromMediaImage(m, proxy.imageInfo.rotationDegrees)).addOnSuccessListener { codes ->
                            codes.firstOrNull()?.rawValue?.let { if (!locked) { locked = true; scanned = it } }
                        }.addOnCompleteListener { proxy.close() }
                    } ?: proxy.close()
                }
                provider.unbindAll(); provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            }, exec)
            pv
        }, modifier = Modifier.fillMaxSize())

        scanned?.let { text ->
            Surface(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp), shape = MaterialTheme.shapes.large, tonalElevation = 6.dp) {
                Column(Modifier.padding(16.dp)) {
                    Text(text.take(400), maxLines = 7, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { try { if (text.startsWith("BEGIN:VCARD")) { val uri = ImageHelper.saveTextToCache(context, text, "scanned_contact.vcf") ?: return; val intent = Intent(Intent.ACTION_VIEW).apply { setDataAndType(uri, "text/x-vcard"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }; context.startActivity(intent) } else { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(text)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }) } catch (_: Exception) {} } }) { Text(stringResource(R.string.scanner_open)) }
                        OutlinedButton(onClick = { scanned = null; locked = false }) { Text(stringResource(R.string.scanner_again)) }
                    }
                }
            }
        }
    }
}
