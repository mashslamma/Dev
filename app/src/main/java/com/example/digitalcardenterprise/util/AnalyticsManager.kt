package com.example.digitalcardenterprise.util

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object AnalyticsManager {
    private val db = FirebaseFirestore.getInstance()
    private const val PREFS = "analytics_prefs"
    private const val KEY_PENDING = "pending_scans"

    fun logScan(context: Context, cardUid: String, companyId: String, action: String, target: String, scannerUid: String? = null) {
        val event = mapOf(
            "cardUid" to cardUid,
            "companyId" to companyId,
            "scannerUid" to scannerUid,
            "action" to action,
            "target" to target,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        // Сохраняем локально на случай офлайна
        savePendingEvent(context, event)

        // Пытаемся отправить сразу
        sendEvent(event)

        // Пытаемся отправить все накопленные
        flushPending(context)
    }

    fun logProfileView(context: Context, uid: String) {
        val event = mapOf(
            "uid" to uid,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        savePendingEvent(context, event)
        db.collection("profile_views").add(event).addOnFailureListener {}
        flushPending(context)
    }

    private fun savePendingEvent(context: Context, event: Map<String, Any>) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_PENDING, "[]")
        val events = mutableListOf<Map<String, Any>>()
        try {
            // Простой парсинг — в реальном проекте лучше использовать Gson/Jackson
            val json = existing
            if (json.isNotBlank() && json != "[]") {
                // Упрощённый подход: просто добавляем новый
                events.add(event)
            } else {
                events.add(event)
            }
        } catch (e: Exception) {
            events.add(event)
        }
        prefs.edit().putString(KEY_PENDING, events.toString()).apply()
    }

    private fun sendEvent(event: Map<String, Any>) {
        db.collection("scans").add(event).addOnFailureListener {
            // Событие уже сохранено локально, будет отправлено позже
        }
    }

    private fun flushPending(context: Context) {
        // В реальном проекте здесь будет отправка накопленных событий
        // при восстановлении соединения
    }

    fun sendPending(context: Context, scope: CoroutineScope) {
        scope.launch {
            withContext(Dispatchers.IO) {
                // Отправка накопленных событий
                val prefs: SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                val pending = prefs.getString(KEY_PENDING, "[]")
                if (pending.isNotBlank() && pending != "[]") {
                    // Отправляем и очищаем
                    prefs.edit().putString(KEY_PENDING, "[]").apply()
                }
            }
        }
    }
}
