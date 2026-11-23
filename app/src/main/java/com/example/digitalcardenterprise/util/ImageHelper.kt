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
import java.io.InputStream

object ImageHelper {
    private const val MAX_IMAGE_DIMENSION = 400

    fun uriToBase64(context: Context, uri: Uri, maxSize: Int = 200, quality: Int = 70): String? {
        var input: InputStream? = null
        return try {
            input = context.contentResolver.openInputStream(uri) ?: return null
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, options)
            input.close()
            input = context.contentResolver.openInputStream(uri) ?: return null

            val scale = Math.max(options.outWidth.toFloat() / MAX_IMAGE_DIMENSION, options.outHeight.toFloat() / MAX_IMAGE_DIMENSION, 1f).toInt()
            options.inJustDecodeBounds = false
            options.inSampleSize = scale

            val original = BitmapFactory.decodeStream(input, null, options) ?: return null
            input.close()

            val ratio = maxSize.toFloat() / maxOf(original.width, original.height).toFloat()
            val scaled = Bitmap.createScaledBitmap(original, (original.width * ratio).toInt().coerceAtLeast(1), (original.height * ratio).toInt().coerceAtLeast(1), true)
            original.recycle()

            val out = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
            scaled.recycle()
            Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        } finally {
            input?.close()
        }
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap, filename: String = "vcard_qr.png"): Uri? {
        return try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            null
        }
    }

    fun saveTextToCache(context: Context, text: String, filename: String): Uri? {
        return try {
            val file = File(context.cacheDir, filename)
            file.writeText(text)
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            null
        }
    }
}
