package com.example.digitalcardenterprise.ui.security

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricLockScreen(onUnlock: () -> Unit) {
    val context = LocalContext.current
    var unlocked by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val activity = context as? androidx.fragment.app.FragmentActivity
        val lifecycle = activity?.lifecycle
        val prompt = BiometricPrompt(
            activity,
            lifecycle,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    error = errString.toString()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    unlocked = true
                    onUnlock()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    error = "Попробуйте снова"
                }
            }
        )

        val cryptoObj = BiometricPrompt.CryptoObject(
            javax.crypto.Cipher.getInstance(javax.crypto.Cipher.ENCRYPT_MODE)
        )
        cryptoObj.cipher.init(javax.crypto.Cipher.ENCRYPT_MODE,
            javax.crypto.KeyGenerator.getInstance("AES").generateKey())

        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Биометрическая блокировка")
                .setSubtitle("Подтвердите личность для доступа к визитке")
                .setNegativeButtonText("Отмена")
                .build(),
            cryptoObj
        )
    }

    if (unlocked) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text("Биометрическая блокировка", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Подтвердите отпечаток пальца или лицо", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}
