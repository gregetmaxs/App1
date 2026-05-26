package com.kasirku.pro.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageDir = File(context.getExternalFilesDir(null), "Pictures")
        if (!imageDir.exists()) imageDir.mkdirs()
        return File(imageDir, "IMG_${timeStamp}.jpg")
    }

    fun compressImage(context: Context, uri: Uri, maxWidth: Int = 800, quality: Int = 80): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val ratio = maxWidth.toFloat() / original.width.toFloat()
            val newHeight = (original.height * ratio).toInt()
            val scaled = Bitmap.createScaledBitmap(original, maxWidth, newHeight, true)

            val outputFile = createImageFile(context)
            val outputStream = FileOutputStream(outputFile)
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            original.recycle()
            scaled.recycle()

            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
