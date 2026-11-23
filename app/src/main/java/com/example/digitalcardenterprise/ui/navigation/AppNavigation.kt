package com.example.digitalcardenterprise.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitalcardenterprise.data.UserProfile
import com.example.digitalcardenterprise.ui.admin.AdminScreen
import com.example.digitalcardenterprise.ui.admin.IconsAdmin
import com.example.digitalcardenterprise.ui.analytics.AnalyticsExportScreen
import com.example.digitalcardenterprise.ui.auth.AuthScreen
import com.example.digitalcardenterprise.ui.history.ScanHistoryScreen
import com.example.digitalcardenterprise.ui.import.ContactImportScreen
import com.example.digitalcardenterprise.ui.profile.ProfileScreen
import com.example.digitalcardenterprise.ui.qr.QrScreen
import com.example.digitalcardenterprise.ui.qr.QrTemplateScreen
import com.example.digitalcardenterprise.ui.qr.QrTemplateStyle
import com.example.digitalcardenterprise.ui.scanner.ScannerScreen
import com.example.digitalcardenterprise.ui.security.BiometricLockScreen
import com.example.digitalcardenterprise.ui.settings.SettingsScreen
import com.example.digitalcardenterprise.ui.team.TeamMemberScreen
import com.example.digitalcardenterprise.ui.web.WebCardScreen
import com.example.digitalcardenterprise.util.ImageHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import android.content.Intent
import android.net.Uri

object Routes {
    const val AUTH = "auth"
    const val PROFILE = "profile"
    const val QR = "qr"
    const val SCANNER = "scanner"
    const val SETTINGS = "settings"
    const val ADMIN = "admin"
    const val WEB_CARD = "web_card"
    const val QR_TEMPLATE = "qr_template"
    const val BIOMETRIC = "biometric"
    const val SCAN_HISTORY = "scan_history"
    const val TEAM = "team"
    const val CONTACT_IMPORT = "contact_import"
    const val ANALYTICS_EXPORT = "analytics_export"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var user by remember { mutableStateOf<FirebaseUser?>(auth.currentUser) }
    var profile by remember { mutableStateOf<UserProfile?>(null) }

    var profileListener by remember { mutableStateOf<com.google.firebase.firestore.ListenerRegistration?>(null) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { user = it.currentUser }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
            profileListener?.remove()
        }
    }

    LaunchedEffect(user?.uid) {
        val current = user
        profileListener?.remove()
        profileListener = null

        if (current == null) {
            profile = null
            nav.navigate(Routes.AUTH) { popUpTo(nav.graph.id) { inclusive = true } }
        } else {
            val uid = current.uid
            profileListener = db.collection("users").document(uid).addSnapshotListener { snap, _ ->
                val loaded = snap?.toObject(UserProfile::class.java) ?: UserProfile(uid = uid, email = current.email ?: "")
                profile = loaded.copy(uid = uid, email = if (loaded.email.isBlank()) current.email ?: "" else loaded.email)
            }
            nav.navigate(Routes.PROFILE) { popUpTo(Routes.AUTH) { inclusive = true } }
        }
    }

    fun saveProfile(newProfile: UserProfile) {
        val uid = user?.uid ?: return
        profile = newProfile.copy(uid = uid)
        db.collection("users").document(uid).set(newProfile.copy(uid = uid), SetOptions.merge())
    }

    NavHost(navController = nav, startDestination = if (auth.currentUser == null) Routes.AUTH else Routes.PROFILE) {
        composable(Routes.AUTH) { AuthScreen(onSuccess = { user = auth.currentUser }) }
        composable(Routes.PROFILE) {
            val p = profile
            if (p == null) Loading() else ProfileScreen(
                profile = p,
                onProfileChange = { saveProfile(it) },
                onOpenQr = { nav.navigate(Routes.QR) },
                onOpenScanner = { nav.navigate(Routes.SCANNER) },
                onOpenSettings = { nav.navigate(Routes.SETTINGS) },
                onOpenAdmin = { if (p.role == "admin") nav.navigate(Routes.ADMIN) },
                onOpenWebCard = { nav.navigate(Routes.WEB_CARD) },
                onOpenQrTemplate = { nav.navigate(Routes.QR_TEMPLATE) },
                onOpenBiometric = { nav.navigate(Routes.BIOMETRIC) },
                onOpenScanHistory = { nav.navigate(Routes.SCAN_HISTORY) },
                onOpenTeam = { if (p.role == "admin") nav.navigate(Routes.TEAM) },
                onOpenContactImport = { nav.navigate(Routes.CONTACT_IMPORT) },
                onOpenAnalyticsExport = { if (p.role == "admin") nav.navigate(Routes.ANALYTICS_EXPORT) },
                onLogout = { auth.signOut() },
                onExportVcf = {
                    val ctx = LocalContext.current
                    val vcard = com.example.digitalcardenterprise.util.VCardGenerator.generate(p)
                    val uri = ImageHelper.saveTextToCache(ctx, vcard, "contact_${p.uid}.vcf") ?: return@composable
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/vcard"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.qr_vcf)))
                }
            )
        }
        composable(Routes.QR) { val p = profile; if (p == null) Loading() else QrScreen(profile = p, onBack = { nav.popBackStack() }) }
        composable(Routes.SCANNER) { ScannerScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN) { val p = profile; if (p == null || p.companyId.isBlank()) Loading() else AdminScreen(companyId = p.companyId, companyName = p.company, onBack = { nav.popBackStack() }, onLogout = { auth.signOut() }) }
        composable(Routes.WEB_CARD) { val p = profile; if (p == null) Loading() else WebCardScreen(profile = p, onBack = { nav.popBackStack() }) }
        composable(Routes.QR_TEMPLATE) { val p = profile; if (p == null) Loading() else QrTemplateScreen(profile = p, selectedTemplate = QrTemplateStyle.CLASSIC, onTemplateChange = {}, onBack = { nav.popBackStack() }) }
        composable(Routes.BIOMETRIC) { BiometricLockScreen(onUnlock = { nav.popBackStack() }) }
        composable(Routes.SCAN_HISTORY) { val p = profile; if (p == null) Loading() else ScanHistoryScreen(cardUid = p.uid, onBack = { nav.popBackStack() }) }
        composable(Routes.TEAM) { val p = profile; if (p == null || p.companyId.isBlank()) Loading() else TeamMemberScreen(companyId = p.companyId, onBack = { nav.popBackStack() }) }
        composable(Routes.CONTACT_IMPORT) { ContactImportScreen(onContactSelected = { firstName, lastName, phone, email ->
            val ctx = LocalContext.current
            val currentProfile = profile
            if (currentProfile != null) {
                saveProfile(currentProfile.copy(
                    firstName = if (currentProfile.firstName.isBlank()) firstName else currentProfile.firstName,
                    lastName = if (currentProfile.lastName.isBlank()) lastName else currentProfile.lastName,
                    phone = if (currentProfile.phone.isBlank()) phone else currentProfile.phone,
                    email = if (currentProfile.email.isBlank()) email else currentProfile.email
                ))
                nav.popBackStack()
            }
        }, onBack = { nav.popBackStack() }) }
        composable(Routes.ANALYTICS_EXPORT) { val p = profile; if (p == null || p.companyId.isBlank()) Loading() else AnalyticsExportScreen(companyId = p.companyId, onBack = { nav.popBackStack() }) }
    }
}

@Composable private fun Loading() { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
