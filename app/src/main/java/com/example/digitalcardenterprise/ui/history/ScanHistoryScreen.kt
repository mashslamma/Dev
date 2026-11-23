package com.example.digitalcardenterprise.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(cardUid: String, onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var scans by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var loading by remember { mutableStateOf(true) }

    androidx.lifecycle.lifecycleScope.launch {
        db.collection("scans")
            .whereEqualTo("cardUid", cardUid)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snap, _ ->
                scans = snap?.documents?.map { it.data } ?: emptyList()
                loading = false
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("История сканирований") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        if (loading) {
            androidx.compose.foundation.layout.Box(Modifier.fillMaxSize(), androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (scans.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text("Пока никто не сканировал вашу визитку", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(scans) { scan ->
                    Card(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(scan["target"]?.toString() ?: "Неизвестно", style = MaterialTheme.typography.bodyMedium)
                                Text(scan["action"]?.toString() ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", scan["timestamp"]).toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
