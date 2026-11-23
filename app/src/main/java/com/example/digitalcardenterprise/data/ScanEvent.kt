package com.example.digitalcardenterprise.data

data class ScanEvent(
    val id: String = "",
    val cardUid: String = "",
    val companyId: String = "",
    val scannerUid: String? = null,
    val action: String = "view",
    val target: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
