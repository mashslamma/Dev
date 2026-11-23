package com.example.digitalcardenterprise.ui.import

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactImportScreen(onContactSelected: (firstName: String, lastName: String, phone: String, email: String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(
        PackageManager.PERMISSION_GRANTED ==
            PackageManager.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
    )}
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    var contacts by remember { mutableStateOf(listOf<Map<String, String>>()) }
    var loading by remember { mutableStateOf(false) }

    if (hasPermission && contacts.isEmpty()) {
        loading = true
        androidx.lifecycle.lifecycleScope.launch {
            val cr: ContentResolver = context.contentResolver
            val cursor: Cursor? = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
            )
            val list = mutableListOf<Map<String, String>>()
            cursor?.use { c ->
                while (c.moveToNext()) {
                    val id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == "1"
                    var phone = ""
                    var email = ""
                    if (hasPhone) {
                        val phones = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id), null
                        )
                        phones?.use { p ->
                            if (p.moveToFirst()) phone = p.getString(p.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        }
                    }
                    val emails = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(id), null
                    )
                    emails?.use { e ->
                        if (e.moveToFirst()) email = e.getString(e.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    }
                    val parts = name?.split(" ") ?: emptyList()
                    list.add(mapOf(
                        "firstName" to parts.firstOrNull() ?: "",
                        "lastName" to parts.lastOrNull() ?: "",
                        "phone" to phone,
                        "email" to email
                    ))
                }
            }
            contacts = list
            loading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Импорт контакта") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        if (!hasPermission) {
            Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text("Нужен доступ к контактам")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }) {
                    Text("Дать разрешение")
                }
            }
        } else if (loading) {
            androidx.compose.foundation.layout.Box(Modifier.fillMaxSize(), androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(contacts) { contact ->
                    ListItem(
                        headlineContent = { Text("${contact["firstName"]} ${contact["lastName"]}") },
                        supportingContent = { Text(contact["phone"] ?: "") },
                        onClick = {
                            onContactSelected(
                                contact["firstName"] ?: "",
                                contact["lastName"] ?: "",
                                contact["phone"] ?: "",
                                contact["email"] ?: ""
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
