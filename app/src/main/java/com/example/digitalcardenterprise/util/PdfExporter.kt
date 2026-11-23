package com.example.digitalcardenterprise.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.digitalcardenterprise.data.UserProfile
import java.io.File
import java.io.FileOutputStream

object PdfExporter {
    fun export(context: Context, profile: UserProfile, qrBitmap: Bitmap): Uri? {
        return try {
            val width = 1050; val height = 583
            val pdf = PdfDocument()
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(width, height, 1).create())
            val c = page.canvas
            c.drawColor(Color.WHITE)
            c.drawRect(0f, 0f, 24f, height.toFloat(), Paint().apply { color = Color.parseColor("#2563EB") })
            c.drawText("${profile.firstName} ${profile.lastName}".trim(), 64f, 100f, Paint().apply { color = Color.BLACK; textSize = 54f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true })
            if (profile.position.isNotBlank()) c.drawText(profile.position, 64f, 150f, Paint().apply { color = Color.DKGRAY; textSize = 30f; isAntiAlias = true })
            if (profile.company.isNotBlank()) c.drawText(profile.company, 64f, 200f, Paint().apply { color = Color.parseColor("#2563EB"); textSize = 34f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true })
            val cp = Paint().apply { color = Color.DKGRAY; textSize = 25f; isAntiAlias = true }
            var y = 270f
            fun drawContact(text: String) { if (text.isBlank()) return; c.drawText(text, 64f, y, cp); y += 40f }
            drawContact("Phone: ${profile.phone}")
            drawContact("Email: ${profile.email}")
            drawContact("Website: ${profile.website}")
            drawContact("Telegram: ${profile.telegram}")
            drawContact("VK: ${profile.vk}")
            drawContact("WeChat: ${profile.wechat}")
            val qrSize = 285; val qrScaled = Bitmap.createScaledBitmap(qrBitmap, qrSize, qrSize, true)
            c.drawBitmap(qrScaled, width - qrSize - 70f, height - qrSize - 70f, null)
            val hintPaint = Paint().apply { color = Color.GRAY; textSize = 20f; textAlign = Paint.Align.CENTER; isAntiAlias = true }
            c.drawText("Scan to save contact", width - qrSize / 2f - 70f, height - 38f, hintPaint)
            pdf.finishPage(page)
            val file = File(context.cacheDir, "digital_card_${profile.uid}.pdf")
            FileOutputStream(file).use { pdf.writeTo(it) }
            pdf.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) { null }
    }
}
