package com.example.digitalcardenterprise.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(companyId: String, companyName: String, onBack: () -> Unit, onLogout: () -> Unit) {
    val ctx = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var scans by remember { mutableStateOf(listOf<String>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(companyId) {
        db.collection("scans").whereEqualTo("companyId", companyId).limit(500).addSnapshotListener { snap, _ ->
            scans = snap?.documents?.map { it.data["target"]?.toString() ?: "" } ?: emptyList()
            loading = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.admin_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }, actions = { IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, null) } }) }) { padding ->
        if (loading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item { Card(Modifier.fillMaxWidth().padding(bottom = 16.dp)) { Column(Modifier.padding(16.dp)) { Text("РЎС‚Р°С‚РёСЃС‚РёРєР° РєРѕРјРїР°РЅРёРё: $companyName", style = MaterialTheme.typography.titleMedium); Spacer(Modifier.height(8.dp)); Row(horizontalArrangement = Arrangement.SpaceBetween) {
                StatBox(stringResource(R.string.admin_total_scans), scans.size.toString(), MaterialTheme.colorScheme.primary)
                StatBox(stringResource(R.string.admin_total_calls), scans.count { it.contains("tel:") }.toString(), MaterialTheme.colorScheme.secondary)
                StatBox(stringResource(R.string.admin_total_shares), scans.count { it.contains("share") }.toString(), MaterialTheme.colorScheme.tertiary)
                StatBox(stringResource(R.string.admin_total_saves), scans.count { it.contains("save") }.toString(), MaterialTheme.colorScheme.surfaceVariant)
            } } } }
            item { Text(stringResource(R.string.admin_recent_scans), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            items(scans) { target -> ListItem(headlineContent = { Text(target) }, supportingContent = { Text("") }); HorizontalDivider() }
        }
    }
}

@Composable private fun StatBox(title: String, value: String, color: Color) { Column(Modifier.weight(1f).padding(horizontal = 4.dp)) { Text(text = value, style = MaterialTheme.typography.headlineMedium, color = color); Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
