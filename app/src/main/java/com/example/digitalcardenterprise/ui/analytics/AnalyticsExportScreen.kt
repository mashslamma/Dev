package com.example.digitalcardenterprise.ui.analytics

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsExportScreen(companyId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var exporting by remember { mutableStateOf(false) }
    var exportedUri by remember { mutableStateOf<Uri?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    fun exportToCsv() {
        exporting = true
        error = null
        Executors.newSingleThreadExecutor().execute {
            try {
                db.collection("scans")
                    .whereEqualTo("companyId", companyId)
                    .get()
                    .await()
                    .documents
                    .let { docs ->
                        if (docs.isEmpty()) {
                            error = "Нет данных для экспорта"
                            exporting = false
                            return@await
                        }
                        val sb = StringBuilder()
                        sb.append("cardUid,scannerUid,action,target,timestamp\n")
                        docs.forEach { doc ->
                            val data = doc.data
                            sb.append("${data["cardUid"] ?: ""},")
                            sb.append("${data["scannerUid"] ?: ""},")
                            sb.append("${data["action"] ?: ""},")
                            sb.append("${data["target"] ?: ""},")
                            sb.append("${data["timestamp"] ?: ""}\n")
                        }
                        val file = File(context.cacheDir, "analytics_${companyId}_${System.currentTimeMillis()}.csv")
                        file.writeText(sb.toString())
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            context, "${context.packageName}.provider", file
                        )
                        exportedUri = uri
                    }
                exporting = false
            } catch (e: Exception) {
                error = e.message
                exporting = false
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Экспорт аналитики") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text("Экспорт данных сканирований в CSV", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { exportToCsv() },
                enabled = !exporting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (exporting) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Экспортировать CSV")
            }

            error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            exportedUri?.let { uri ->
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Экспорт аналитики"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Поделиться отчетом")
                }
            }
        }
    }
}
