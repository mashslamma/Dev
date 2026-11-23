package com.example.digitalcardenterprise.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitalcardenterprise.data.UserProfile
import com.example.digitalcardenterprise.ui.admin.AdminScreen
import com.example.digitalcardenterprise.ui.admin.IconsAdmin
import com.example.digitalcardenterprise.ui.auth.AuthScreen
import com.example.digitalcardenterprise.ui.profile.ProfileScreen
import com.example.digitalcardenterprise.ui.qr.QrScreen
import com.example.digitalcardenterprise.ui.scanner.ScannerScreen
import com.example.digitalcardenterprise.ui.settings.SettingsScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object Routes {
    const val AUTH = "auth"
    const val PROFILE = "profile"
    const val QR = "qr"
    const val SCANNER = "scanner"
    const val SETTINGS = "settings"
    const val ADMIN = "admin"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var user by remember { mutableStateOf<FirebaseUser?>(auth.currentUser) }
    var profile by remember { mutableStateOf<UserProfile?>(null) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { user = it.currentUser }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(user?.uid) {
        val current = user
        if (current == null) {
            profile = null; nav.navigate(Routes.AUTH) { popUpTo(nav.graph.id) { inclusive = true } }
        } else {
            val uid = current.uid
            db.collection("users").document(uid).addSnapshotListener { snap, _ ->
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
            if (p == null) Loading() else ProfileScreen(profile = p, onProfileChange = { saveProfile(it) }, onOpenQr = { nav.navigate(Routes.QR) }, onOpenScanner = { nav.navigate(Routes.SCANNER) }, onOpenSettings = { nav.navigate(Routes.SETTINGS) }, onOpenAdmin = { if (p.role == "admin") nav.navigate(Routes.ADMIN) }, onLogout = { auth.signOut() }, onExportVcf = {})
        }
        composable(Routes.QR) { val p = profile; if (p == null) Loading() else QrScreen(profile = p, onBack = { nav.popBackStack() }) }
        composable(Routes.SCANNER) { ScannerScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN) { val p = profile; if (p == null || p.companyId.isBlank()) Loading() else AdminScreen(companyId = p.companyId, companyName = p.company, onBack = { nav.popBackStack() }, onLogout = { auth.signOut() }) }
    }
}

@Composable private fun Loading() { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
