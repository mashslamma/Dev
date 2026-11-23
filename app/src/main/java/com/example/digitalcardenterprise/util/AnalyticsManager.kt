package com.example.digitalcardenterprise.util

import com.google.firebase.firestore.FirebaseFirestore

object AnalyticsManager {
    private val db = FirebaseFirestore.getInstance()
    fun logScan(cardUid: String, companyId: String, action: String, target: String, scannerUid: String? = null) {
        db.collection("scans").add(mapOf(
            "cardUid" to cardUid, "companyId" to companyId, "scannerUid" to scannerUid,
            "action" to action, "target" to target, "timestamp" to com.google.firebase.Timestamp.now()
        )).addOnFailureListener {}
    }
    fun logProfileView(uid: String) {
        db.collection("profile_views").add(mapOf("uid" to uid, "timestamp" to com.google.firebase.Timestamp.now())).addOnFailureListener {}
    }
}
