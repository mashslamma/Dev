package com.example.digitalcardenterprise.data

import androidx.compose.runtime.Stable

@Stable
data class UserProfile(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val position: String = "",
    val company: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val address: String = "",
    val photoBase64: String = "",
    val telegram: String = "",
    val whatsapp: String = "",
    val linkedin: String = "",
    val instagram: String = "",
    val facebook: String = "",
    val twitter: String = "",
    val vk: String = "",
    val ok: String = "",
    val tenchat: String = "",
    val dzen: String = "",
    val rutube: String = "",
    val yappy: String = "",
    val max: String = "",
    val wechat: String = "",
    val weibo: String = "",
    val douyin: String = "",
    val xiaohongshu: String = "",
    val qq: String = "",
    val bilibili: String = "",
    val line: String = "",
    val kakao: String = "",
    val companyId: String = "",
    val role: String = "member",
    val dynamicCardId: String = "",
    val walletPassId: String = "",
    val nfcEnabled: Boolean = false
) {
    fun isComplete(): Boolean = firstName.isNotBlank() && phone.isNotBlank()
    fun estimatedVCardSize(): Int = com.example.digitalcardenterprise.util.VCardGenerator.generate(this).toByteArray(Charsets.UTF_8).size
    fun toVcfFilename(): String = "digital_card_${if (uid.isNotBlank()) uid else "contact"}.vcf"
}
