package com.example.digitalcardenterprise.util

import android.graphics.Bitmap
import android.util.LruCache

object QrCache {
    private val cache = LruCache<String, Bitmap>(
        maxSize = (Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()
    )

    fun get(key: String): Bitmap? = cache.get(key)

    fun put(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }

    fun remove(key: String) {
        cache.remove(key)
    }

    fun clear() {
        cache.evictAll()
    }
}
