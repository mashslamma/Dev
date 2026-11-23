package com.example.digitalcardenterprise.ui.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.digitalcardenterprise.R
import com.example.digitalcardenterprise.data.UserProfile
import com.example.digitalcardenterprise.util.ImageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile,
    onProfileChange: (UserProfile) -> Unit,
    onOpenQr: () -> Unit,
    onOpenScanner: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAdmin: () -> Unit,
    onLogout: () -> Unit,
    onExportVcf: () -> Unit
) {
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) ImageHelper.uriToBase64(context, uri)?.let { onProfileChange(profile.copy(photoBase64 = it)) }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.profile_title)) }, actions = {
        IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.profile_settings)) }
        if (profile.role == "admin") IconButton(onClick = onOpenAdmin) { Icon(com.example.digitalcardenterprise.ui.admin.IconsAdmin.Default.Analytics, null) }
        IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.profile_logout)) }
    }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(modifier = Modifier.size(120.dp).clip(CircleShape).clickable { photoPicker.launch("image/*") }, color = MaterialTheme.colorScheme.surfaceVariant) {
                if (profile.photoBase64.isNotBlank()) {
                    val bitmap = remember(profile.photoBase64) { val bytes = Base64.decode(profile.photoBase64, Base64.DEFAULT); BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
                    AsyncImage(bitmap, null, Modifier.fillMaxSize())
                } else Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, Modifier.size(56.dp)) }
            }
            Spacer(Modifier.height(24.dp))
            Field(stringResource(R.string.profile_first_name), profile.firstName) { onProfileChange(profile.copy(firstName = it)) }
            Field(stringResource(R.string.profile_last_name), profile.lastName) { onProfileChange(profile.copy(lastName = it)) }
            Field(stringResource(R.string.profile_position), profile.position) { onProfileChange(profile.copy(position = it)) }
            Field(stringResource(R.string.profile_company), profile.company) { onProfileChange(profile.copy(company = it)) }
            Field(stringResource(R.string.profile_phone), profile.phone) { onProfileChange(profile.copy(phone = it)) }
            Field(stringResource(R.string.profile_email), profile.email) { onProfileChange(profile.copy(email = it)) }
            Field("Website", profile.website) { onProfileChange(profile.copy(website = it)) }
            Field("Address", profile.address) { onProfileChange(profile.copy(address = it)) }

            Section(stringResource(R.string.social_international))
            Field(stringResource(R.string.profile_telegram), profile.telegram) { onProfileChange(profile.copy(telegram = it)) }
            Field(stringResource(R.string.profile_whatsapp), profile.whatsapp) { onProfileChange(profile.copy(whatsapp = it)) }
            Field("LinkedIn", profile.linkedin) { onProfileChange(profile.copy(linkedin = it)) }
            Field("Instagram", profile.instagram) { onProfileChange(profile.copy(instagram = it)) }
            Field("Facebook", profile.facebook) { onProfileChange(profile.copy(facebook = it)) }
            Field("X / Twitter", profile.twitter) { onProfileChange(profile.copy(twitter = it)) }

            Section(stringResource(R.string.social_russian))
            Field(stringResource(R.string.profile_vk), profile.vk) { onProfileChange(profile.copy(vk = it)) }
            Field("OK.ru", profile.ok) { onProfileChange(profile.copy(ok = it)) }
            Field("TenChat", profile.tenchat) { onProfileChange(profile.copy(tenchat = it)) }
            Field("Dzen", profile.dzen) { onProfileChange(profile.copy(dzen = it)) }
            Field("Rutube", profile.rutube) { onProfileChange(profile.copy(rutube = it)) }
            Field("Yappy", profile.yappy) { onProfileChange(profile.copy(yappy = it)) }
            Field("MAX", profile.max) { onProfileChange(profile.copy(max = it)) }

            Section(stringResource(R.string.social_chinese))
            Field(stringResource(R.string.profile_wechat), profile.wechat) { onProfileChange(profile.copy(wechat = it)) }
            Field("Weibo", profile.weibo) { onProfileChange(profile.copy(weibo = it)) }
            Field("Douyin", profile.douyin) { onProfileChange(profile.copy(douyin = it)) }
            Field("Xiaohongshu / RED", profile.xiaohongshu) { onProfileChange(profile.copy(xiaohongshu = it)) }
            Field("QQ", profile.qq) { onProfileChange(profile.copy(qq = it)) }
            Field("Bilibili", profile.bilibili) { onProfileChange(profile.copy(bilibili = it)) }

            Section(stringResource(R.string.social_japanese))
            Field(stringResource(R.string.profile_line), profile.line) { onProfileChange(profile.copy(line = it)) }

            Section(stringResource(R.string.social_korean))
            Field(stringResource(R.string.profile_kakao), profile.kakao) { onProfileChange(profile.copy(kakao = it)) }

            Spacer(Modifier.height(16.dp))
            val size = profile.estimatedVCardSize()
            if (size > 2500) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(text = stringResource(R.string.profile_qr_big, size), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(12.dp))
            }

            Button(onClick = onOpenQr, enabled = profile.isComplete(), modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.profile_show_qr)) }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onOpenScanner, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.profile_scan)) }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onExportVcf, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.profile_export_vcf)) }

            if (!profile.isComplete()) { Spacer(Modifier.height(8.dp)); Text(text = stringResource(R.string.profile_fill_required), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(32.dp))
        }
    }

    @Composable private fun Section(title: String) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 4.dp))
    }

    @Composable private fun Field(label: String, value: String, onChange: (String) -> Unit) {
        OutlinedTextField(value, onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
    }
}
