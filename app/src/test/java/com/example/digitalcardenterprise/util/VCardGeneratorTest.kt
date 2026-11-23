package com.example.digitalcardenterprise.util

import com.example.digitalcardenterprise.data.UserProfile
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VCardGeneratorTest {

    @Test
    fun generate_shouldContainBasicFields() {
        val profile = UserProfile(
            firstName = "Иван",
            lastName = "Иванов",
            phone = "+79991234567",
            email = "ivan@example.com",
            telegram = "ivan_ivanov",
            vk = "ivan_ivanov"
        )
        val vcard = VCardGenerator.generate(profile)

        assertNotNull(vcard)
        assertTrue(vcard.contains("BEGIN:VCARD"))
        assertTrue(vcard.contains("VERSION:3.0"))
        assertTrue(vcard.contains("FN:Иван Иванов"))
        assertTrue(vcard.contains("N:Иванов;Иван;;;"))
        assertTrue(vcard.contains("TEL;TYPE=CELL:+79991234567"))
        assertTrue(vcard.contains("EMAIL;TYPE=INTERNET:ivan@example.com"))
        assertTrue(vcard.contains("URL:https://t.me/ivan_ivanov"))
        assertTrue(vcard.contains("URL:https://vk.com/ivan_ivanov"))
        assertTrue(vcard.contains("END:VCARD"))
    }

    @Test
    fun generate_shouldEscapeSpecialCharacters() {
        val profile = UserProfile(
            firstName = "Иван,Петр",
            lastName = "Иванов;Сергеевич",
            phone = "+79991234567"
        )
        val vcard = VCardGenerator.generate(profile)

        assertTrue(vcard.contains("N:Иванов\\;Сергеевич;Иван\\,Петр;;;"))
    }

    @Test
    fun generate_withPhoto_shouldContainPhotoField() {
        val profile = UserProfile(
            firstName = "Test",
            lastName = "User",
            phone = "+79991234567",
            photoBase64 = "AAAA"
        )
        val vcard = VCardGenerator.generate(profile)

        assertTrue(vcard.contains("PHOTO;ENCODING=b;TYPE=JPEG:"))
    }

    @Test
    fun generate_sizeLimit_shouldTruncatePhoto() {
        val profile = UserProfile(
            firstName = "A".repeat(500),
            lastName = "B".repeat(500),
            phone = "+79991234567",
            photoBase64 = "A".repeat(10000)
        )
        val vcard = VCardGenerator.generate(profile)

        assertTrue(!vcard.contains("PHOTO;ENCODING=b"))
    }

    @Test
    fun generate_allSocialNetworks_shouldContainAllUrls() {
        val profile = UserProfile(
            firstName = "Test",
            lastName = "User",
            phone = "+79991234567",
            telegram = "test_tg",
            whatsapp = "79991234567",
            vk = "test_vk",
            ok = "test_ok",
            wechat = "test_wechat",
            weibo = "test_weibo",
            douyin = "test_douyin",
            line = "test_line",
            kakao = "test_kakao"
        )
        val vcard = VCardGenerator.generate(profile)

        assertTrue(vcard.contains("URL:https://t.me/test_tg"))
        assertTrue(vcard.contains("URL:https://wa.me/79991234567"))
        assertTrue(vcard.contains("URL:https://vk.com/test_vk"))
        assertTrue(vcard.contains("URL:https://ok.ru/profile/test_ok"))
        assertTrue(vcard.contains("URL:https://weibo.com/test_weibo"))
        assertTrue(vcard.contains("URL:https://v.douyin.com/test_douyin"))
        assertTrue(vcard.contains("URL:https://line.me/ti/p/test_line"))
        assertTrue(vcard.contains("URL:https://kakao.com/test_kakao"))
    }
}
