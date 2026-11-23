package com.example.digitalcardenterprise.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Hashtable

object QrGenerator {
    fun generate(text: String, size: Int = 900, errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.M): Bitmap? {
        return try {
            val hints = Hashtable<EncodeHintType, Any>().apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel)
                put(EncodeHintType.MARGIN, 2)
            }
            val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val w = matrix.width; val h = matrix.height
            val px = IntArray(w * h)
            for (y in 0 until h) for (x in 0 until w)
                px[y * w + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply { setPixels(px, 0, w, 0, 0, w, h) }
        } catch (e: Exception) { null }
    }
}
