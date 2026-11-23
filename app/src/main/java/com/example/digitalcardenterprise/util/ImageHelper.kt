package com.example.digitalcardenterprise.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageHelper {
    fun uriToBase64(context: Context, uri: Uri, maxSize: Int = 200, quality: Int = 70): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            val original = BitmapFactory.decodeStream(input); input.close()
            if (original == null) return null
            val ratio = maxSize.toFloat() / maxOf(original.width, original.height).toFloat()
            val scaled = Bitmap.createScaledBitmap(original, (original.width * ratio).toInt().coerceAtLeast(1), (original.height * ratio).toInt().coerceAtLeast(1), true)
            val out = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
            Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) { null }
    }
    fun saveBitmapToCache(context: Context, bitmap: Bitmap, filename: String = "vcard_qr.png"): Uri? {
        return try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) { null }
    }
    fun saveTextToCache(context: Context, text: String, filename: String): Uri? {
        return try {
            val file = File(context.cacheDir, filename)
            file.writeText(text)
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) { null }
    }
}
