package com.example.digitalcardenterprise.ui.web

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.digitalcardenterprise.data.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebCardScreen(profile: UserProfile, onBack: () -> Unit) {
    val html = buildWebCardHtml(profile)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Веб-визитка") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        )
    }
}

private fun buildWebCardHtml(p: UserProfile): String = """
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<style>
  * { margin:0; padding:0; box-sizing:border-box; font-family:-apple-system,Segoe UI,Roboto,sans-serif; }
  body { background:linear-gradient(135deg,#667eea,#764ba2); min-height:100vh; padding:20px; }
  .card { background:white; border-radius:24px; padding:32px; max-width:420px; margin:40px auto; box-shadow:0 20px 60px rgba(0,0,0,.3); }
  .avatar { width:120px; height:120px; border-radius:50%; margin:0 auto 16px; background:#eee; object-fit:cover; }
  h1 { text-align:center; font-size:24px; margin-bottom:4px; }
  .pos { text-align:center; color:#666; margin-bottom:8px; }
  .comp { text-align:center; color:#2563eb; font-weight:600; margin-bottom:24px; }
  .links { display:flex; flex-direction:column; gap:10px; }
  a { display:block; padding:14px 18px; background:#f3f4f6; border-radius:12px; text-decoration:none; color:#111; font-weight:500; transition:.2s; }
  a:hover { background:#e5e7eb; transform:translateY(-2px); }
  .tg { background:#229ED9; color:white; }
  .wa { background:#25D366; color:white; }
  .vk { background:#0077FF; color:white; }
</style></head><body>
<div class="card">
  <h1>${p.firstName} ${p.lastName}</h1>
  ${if (p.position.isNotEmpty()) "<div class='pos'>${p.position}</div>" else ""}
  ${if (p.company.isNotEmpty()) "<div class='comp'>${p.company}</div>" else ""}
  <div class="links">
    ${if (p.phone.isNotEmpty()) "<a href='tel:${p.phone}'>📞 ${p.phone}</a>" else ""}
    ${if (p.email.isNotEmpty()) "<a href='mailto:${p.email}'>✉️ ${p.email}</a>" else ""}
    ${if (p.telegram.isNotEmpty()) "<a class='tg' href='https://t.me/${p.telegram}'>Telegram</a>" else ""}
    ${if (p.whatsapp.isNotEmpty()) "<a class='wa' href='https://wa.me/${p.whatsapp}'>WhatsApp</a>" else ""}
    ${if (p.vk.isNotEmpty()) "<a class='vk' href='https://vk.com/${p.vk}'>ВКонтакте</a>" else ""}
    ${if (p.instagram.isNotEmpty()) "<a href='https://instagram.com/${p.instagram}'>Instagram</a>" else ""}
    ${if (p.linkedin.isNotEmpty()) "<a href='https://linkedin.com/in/${p.linkedin}'>LinkedIn</a>" else ""}
    ${if (p.website.isNotEmpty()) "<a href='${p.website}'>🌐 Сайт</a>" else ""}
  </div>
</div></body></html>
""".trimIndent()
