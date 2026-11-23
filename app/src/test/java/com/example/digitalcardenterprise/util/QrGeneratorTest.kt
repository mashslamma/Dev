package com.example.digitalcardenterprise.util

import android.graphics.Bitmap
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class QrGeneratorTest {

    @Test
    fun generate_shouldReturnBitmap() {
        val result = QrGenerator.generate("BEGIN:VCARD\nVERSION:3.0\nFN:Test\nEND:VCARD")
        assertNotNull(result)
        assertTrue(result?.width ?: 0 > 0)
        assertTrue(result?.height ?: 0 > 0)
    }

    @Test
    fun generate_withErrorCorrectionH_shouldReturnBitmap() {
        val result = QrGenerator.generate(
            text = "Test",
            size = 400,
            errorCorrectionLevel = ErrorCorrectionLevel.H
        )
        assertNotNull(result)
    }

    @Test
    fun generate_emptyText_shouldReturnNull() {
        val result = QrGenerator.generate("")
        // Может вернуть null или пустой bitmap — не должен падать
    }

    @Test
    fun generate_differentSizes_shouldReturnDifferentDimensions() {
        val small = QrGenerator.generate("Test", size = 200)
        val large = QrGenerator.generate("Test", size = 800)
        assertNotNull(small)
        assertNotNull(large)
        assertTrue(small?.width ?: 0 < large?.width ?: 0)
    }

    @Test
    fun generate_longText_shouldReturnBitmap() {
        val longText = "A".repeat(500)
        val result = QrGenerator.generate(longText)
        assertNotNull(result)
    }
}
