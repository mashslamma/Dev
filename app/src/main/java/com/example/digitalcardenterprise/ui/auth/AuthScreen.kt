package com.example.digitalcardenterprise.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.digitalcardenterprise.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(onSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.auth_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(email, { email = it }, label = { Text(stringResource(R.string.auth_email)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(password, { password = it }, label = { Text(stringResource(R.string.auth_password)) }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            loading = true; error = null; message = null
            val task = if (isLogin) auth.signInWithEmailAndPassword(email.trim(), password) else auth.createUserWithEmailAndPassword(email.trim(), password)
            task.addOnSuccessListener { loading = false; onSuccess() }.addOnFailureListener { loading = false; error = it.localizedMessage ?: it.message }
        }, enabled = !loading && email.contains("@") && password.length >= 6, modifier = Modifier.fillMaxWidth()) {
            Text(if (isLogin) stringResource(R.string.auth_sign_in) else stringResource(R.string.auth_sign_up))
        }
        TextButton(onClick = { isLogin = !isLogin; error = null; message = null }) {
            Text(if (isLogin) stringResource(R.string.auth_no_account) else stringResource(R.string.auth_has_account))
        }
        TextButton(enabled = email.contains("@") && !loading, onClick = {
            loading = true; error = null; message = null
            auth.sendPasswordResetEmail(email.trim()).addOnSuccessListener { loading = false; message = "OK" }.addOnFailureListener { loading = false; error = it.localizedMessage ?: it.message }
        }) { Text(stringResource(R.string.auth_reset_password)) }
        if (loading) { Spacer(Modifier.height(12.dp)); CircularProgressIndicator() }
        if (message != null) { Spacer(Modifier.height(12.dp)); Text(text = stringResource(R.string.auth_reset_sent), color = MaterialTheme.colorScheme.primary) }
        if (error != null) { Spacer(Modifier.height(12.dp)); Text(text = error ?: "", color = MaterialTheme.colorScheme.error) }
    }
}
