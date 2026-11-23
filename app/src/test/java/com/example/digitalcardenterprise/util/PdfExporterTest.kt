package com.example.digitalcardenterprise.util

import android.graphics.Bitmap
import com.example.digitalcardenterprise.data.UserProfile
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PdfExporterTest {

    @Test
    fun profile_isComplete_whenNameAndPhoneFilled() {
        val profile = UserProfile(
            firstName = "Иван",
            lastName = "Иванов",
            phone = "+79991234567"
        )
        assertTrue(profile.isComplete())
    }

    @Test
    fun profile_isNotComplete_whenNameMissing() {
        val profile = UserProfile(
            firstName = "",
            lastName = "Иванов",
            phone = "+79991234567"
        )
        assertTrue(!profile.isComplete())
    }

    @Test
    fun profile_isNotComplete_whenPhoneMissing() {
        val profile = UserProfile(
            firstName = "Иван",
            lastName = "Иванов",
            phone = ""
        )
        assertTrue(!profile.isComplete())
    }

    @Test
    fun estimatedVCardSize_shouldReturnPositiveValue() {
        val profile = UserProfile(
            firstName = "Иван",
            lastName = "Иванов",
            phone = "+79991234567",
            email = "ivan@example.com",
            telegram = "ivan_ivanov",
            vk = "ivan_ivanov"
        )
        val size = profile.estimatedVCardSize()
        assertTrue(size > 0)
    }

    @Test
    fun qrBitmap_creation_shouldNotThrow() {
        val qrBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        assertNotNull(qrBitmap)
        assertTrue(qrBitmap.width == 100)
        assertTrue(qrBitmap.height == 100)
    }
}
