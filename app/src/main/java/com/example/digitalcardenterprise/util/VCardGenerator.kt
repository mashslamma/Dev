package com.example.digitalcardenterprise.util

import com.example.digitalcardenterprise.data.UserProfile

object VCardGenerator {
    fun generate(p: UserProfile): String {
        val lines = mutableListOf<String>()
        lines += "BEGIN:VCARD"
        lines += "VERSION:3.0"
        lines += "PRODID:-//QRCardPro//EN"
        lines += "N:${esc(p.lastName)};${esc(p.firstName)};;;"
        lines += "FN:${esc((p.firstName + " " + p.lastName).trim())}"
        if (p.company.isNotBlank()) lines += "ORG:${esc(p.company)}"
        if (p.position.isNotBlank()) lines += "TITLE:${esc(p.position)}"
        if (p.phone.isNotBlank()) lines += "TEL;TYPE=CELL:${p.phone.filterPhone()}"
        if (p.email.isNotBlank()) lines += "EMAIL;TYPE=INTERNET:${p.email.trim()}"
        if (p.website.isNotBlank()) lines += "URL:${urlOrPrefix(p.website, "")}"
        if (p.address.isNotBlank()) lines += "ADR;TYPE=WORK:;;${esc(p.address)};;;;"
        addUrl(lines, p.telegram, "https://t.me/", "telegram")
        addUrl(lines, p.whatsapp.filterDigits(), "https://wa.me/", null)
        addUrl(lines, p.linkedin, "https://linkedin.com/in/", "linkedin")
        addUrl(lines, p.instagram, "https://instagram.com/", "instagram")
        addUrl(lines, p.facebook, "https://facebook.com/", "facebook")
        addUrl(lines, p.twitter, "https://x.com/", "twitter")
        addUrl(lines, p.vk, "https://vk.com/", "vk")
        addUrl(lines, p.ok, "https://ok.ru/profile/", null)
        addUrl(lines, p.tenchat, "https://tenchat.ru/", null)
        addUrl(lines, p.dzen, "https://dzen.ru/", null)
        addUrl(lines, p.rutube, "https://rutube.ru/channel/", null)
        addUrl(lines, p.yappy, "https://yappy.media/", null)
        addUrl(lines, p.max, "https://max.ru/", null)
        addUrl(lines, p.weibo, "https://weibo.com/", null)
        addUrl(lines, p.douyin, "https://v.douyin.com/", null)
        addUrl(lines, p.xiaohongshu, "https://www.xiaohongshu.com/user/profile/", null)
        addUrl(lines, p.qq.filterDigits(), "https://wpa.qq.com/msgrd?v=3&uin=", null)
        addUrl(lines, p.bilibili, "https://space.bilibili.com/", null)
        addUrl(lines, p.line, "https://line.me/ti/p/", null)
        addUrl(lines, p.kakao, "https://kakao.com/", null)
        if (p.dynamicCardId.isNotBlank()) lines += "URL:https://card.digitalcardenterprise.com/${p.dynamicCardId}"
        val note = buildString {
            fun line(label: String, value: String) { if (value.isNotBlank()) append("$label: $value\n") }
            line("Telegram", p.telegram); line("WhatsApp", p.whatsapp); line("VK", p.vk); line("OK", p.ok)
            line("TenChat", p.tenchat); line("Dzen", p.dzen); line("Rutube", p.rutube); line("Yappy", p.yappy)
            line("MAX", p.max); line("WeChat", p.wechat); line("Weibo", p.weibo); line("Douyin", p.douyin)
            line("Xiaohongshu", p.xiaohongshu); line("QQ", p.qq); line("Bilibili", p.bilibili); line("LINE", p.line)
            line("KakaoTalk", p.kakao)
        }
        if (note.isNotBlank()) lines += "NOTE:${esc(note.trim())}"
        if (p.photoBase64.isNotBlank()) lines += "PHOTO;ENCODING=b;TYPE=JPEG:${p.photoBase64}"
        lines += "END:VCARD"
        return lines.joinToString("\r\n") + "\r\n"
    }
    private fun addUrl(lines: MutableList<String>, value: String, prefix: String, iosType: String?) {
        if (value.isBlank()) return
        val url = urlOrPrefix(value, prefix)
        lines += "URL:$url"
        if (iosType != null) lines += "X-SOCIALPROFILE;TYPE=$iosType:$url"
    }
    private fun urlOrPrefix(value: String, prefix: String): String {
        val v = value.trim().removePrefix("@")
        return if (v.startsWith("http://") || v.startsWith("https://") || v.contains("://")) v else prefix + v
    }
    private fun String.filterDigits(): String = filter { it.isDigit() }
    private fun String.filterPhone(): String = filter { it.isDigit() || it == '+' }
    private fun esc(s: String): String = s.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "").replace(",", "\\,").replace(";", "\\;")
}
