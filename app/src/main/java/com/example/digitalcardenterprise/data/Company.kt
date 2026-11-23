package com.example.digitalcardenterprise.data

data class Company(
    val id: String = "",
    val name: String = "",
    val ownerUid: String = "",
    val primaryColor: String = "#2563EB",
    val logoUrl: String = "",
    val plan: String = "free",
    val createdAt: Long = System.currentTimeMillis()
)
