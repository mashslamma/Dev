package com.example.digitalcardenterprise.ui.qr

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import com.example.digitalcardenterprise.ui.qr.QrTemplateStyle
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Hashtable

object QrGeneratorWithTemplate {
    fun generateWithTemplate(
        text: String,
        style: QrTemplateStyle,
        size: Int = 800
    ): Bitmap? {
        return try {
            val hints = Hashtable<EncodeHintType, Any>().apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.MARGIN, 2)
            }
            val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val w = matrix.width
            val h = matrix.height
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            val paint = Paint().apply { isAntiAlias = true }

            canvas.drawColor(Color.WHITE)

            when (style) {
                QrTemplateStyle.CLASSIC -> {
                    paint.color = Color.BLACK
                    for (y in 0 until h) for (x in 0 until w) {
                        if (matrix.get(x, y)) canvas.drawRect(x.toFloat(), y.toFloat(), (x + 1).toFloat(), (y + 1).toFloat(), paint)
                    }
                }
                QrTemplateStyle.ROUNDED -> {
                    paint.color = Color.BLACK
                    for (y in 0 until h) for (x in 0 until w) {
                        if (matrix.get(x, y)) canvas.drawRoundRect(x.toFloat(), y.toFloat(), (x + 1).toFloat(), (y + 1).toFloat(), 2f, 2f, paint)
                    }
                }
                QrTemplateStyle.GRADIENT -> {
                    val gradient = LinearGradient(0f, 0f, size.toFloat(), size.toFloat(),
                        intArrayOf(Color.parseColor("#667eea"), Color.parseColor("#764ba2")),
                        null, Shader.TileMode.CLAMP)
                    paint.shader = gradient
                    for (y in 0 until h) for (x in 0 until w) {
                        if (matrix.get(x, y)) canvas.drawRect(x.toFloat(), y.toFloat(), (x + 1).toFloat(), (y + 1).toFloat(), paint)
                    }
                }
                QrTemplateStyle.FRAMED -> {
                    paint.color = Color.BLACK
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = size * 0.02f
                    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
                    paint.style = Paint.Style.FILL
                    for (y in 0 until h) for (x in 0 until w) {
                        if (matrix.get(x, y)) canvas.drawRect(x.toFloat(), y.toFloat(), (x + 1).toFloat(), (y + 1).toFloat(), paint)
                    }
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
