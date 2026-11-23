package com.example.digitalcardenterprise.util

import com.google.firebase.firestore.FirebaseFirestore

object DynamicQrManager {
    private val db = FirebaseFirestore.getInstance()
    fun createCardId(uid: String): String = db.collection("dynamic_cards").document().id
    fun saveDynamicCard(cardId: String, data: Map<String, Any>) {
        db.collection("dynamic_cards").document(cardId).set(data, com.google.firebase.firestore.SetOptions.merge())
    }
    fun getDynamicCard(cardId: String, listener: (Map<String, Any>?) -> Unit) {
        db.collection("dynamic_cards").document(cardId).addSnapshotListener { snap, _ -> listener(snap?.data) }
    }
}
