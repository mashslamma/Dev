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
    private const val WIDTH = 1050f
    private const val HEIGHT = 583f
    private const val MARGIN = 64f

    fun export(context: Context, profile: UserProfile, qrBitmap: Bitmap): Uri? {
        return try {
            val width = WIDTH.toInt()
            val height = HEIGHT.toInt()
            val pdf = PdfDocument()
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(width, height, 1).create())
            val c = page.canvas
            c.drawColor(Color.WHITE)

            c.drawRect(0f, 0f, 24f, height.toFloat(), Paint().apply { color = Color.parseColor("#2563EB") })

            val namePaint = Paint().apply { color = Color.BLACK; textSize = 54f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }
            val nameText = "${profile.firstName} ${profile.lastName}".trim()
            val nameWidth = namePaint.measureText(nameText)
            val nameFontSize = if (nameWidth > WIDTH - 200f) 32f else 54f
            namePaint.textSize = nameFontSize
            c.drawText(nameText, MARGIN, 100f, namePaint)

            val subPaint = Paint().apply { color = Color.DKGRAY; textSize = 30f; isAntiAlias = true }
            if (profile.position.isNotBlank()) {
                val posText = profile.position
                val posWidth = subPaint.measureText(posText)
                if (posWidth > WIDTH - MARGIN * 2) {
                    subPaint.textSize = 20f
                }
                c.drawText(posText, MARGIN, 150f, subPaint)
                subPaint.textSize = 30f
            }

            val companyPaint = Paint().apply { color = Color.parseColor("#2563EB"); textSize = 34f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }
            if (profile.company.isNotBlank()) {
                val compText = profile.company
                val compWidth = companyPaint.measureText(compText)
                if (compWidth > WIDTH - MARGIN * 2) {
                    companyPaint.textSize = 24f
                }
                c.drawText(compText, MARGIN, 200f, companyPaint)
                companyPaint.textSize = 34f
            }

            val contactPaint = Paint().apply { color = Color.DKGRAY; textSize = 25f; isAntiAlias = true }
            var y = 270f
            fun drawContact(label: String, value: String) {
                val text = "$label: $value"
                if (text.isBlank()) return
                val textWidth = contactPaint.measureText(text)
                var fontSize = 25f
                if (textWidth > WIDTH - MARGIN * 2 - 200f) {
                    fontSize = 18f
                    contactPaint.textSize = fontSize
                }
                c.drawText(text, MARGIN, y, contactPaint)
                contactPaint.textSize = 25f
                y += 40f
            }

            drawContact("Phone", profile.phone)
            drawContact("Email", profile.email)
            drawContact("Website", profile.website)
            drawContact("Telegram", profile.telegram)
            drawContact("VK", profile.vk)
            drawContact("WeChat", profile.wechat)

            val qrSize = 285
            val qrScaled = Bitmap.createScaledBitmap(qrBitmap, qrSize, qrSize, true)
            c.drawBitmap(qrScaled, width - qrSize - 70f, height - qrSize - 70f, null)

            val hintPaint = Paint().apply { color = Color.GRAY; textSize = 20f; textAlign = Paint.Align.CENTER; isAntiAlias = true }
            c.drawText("Scan to save contact", width - qrSize / 2f - 70f, height - 38f, hintPaint)

            pdf.finishPage(page)
            val file = File(context.cacheDir, "digital_card_${profile.uid}.pdf")
            FileOutputStream(file).use { pdf.writeTo(it) }
            pdf.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            null
        }
    }
}
