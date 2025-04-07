package com.weit2nd.deepfakedetector.data.source.localimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalImageDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun getResizedBitmap(
        uri: String,
        width: Int,
        height: Int,
    ) = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(Uri.parse(uri)).use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
    }

    suspend fun preprocessBitmapToCHW(
        uri: String,
        width: Int,
        height: Int,
    ): FloatArray {
        val size = width * height
        val bitmap = getResizedBitmap(
            uri = uri,
            width = width,
            height = height,
        )
        val floatValues = FloatArray(3 * size)
        val pixels = IntArray(size)
        bitmap.getPixels(
            /* pixels = */ pixels,
            /* offset = */ 0,
            /* stride = */ width,
            /* x = */ 0,
            /* y = */ 0,
            /* width = */ width,
            /* height = */ height,
        )

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            val x = i % width
            val y = i / width
            val idx = y * width + x

            floatValues[idx] = r
            floatValues[size + idx] = g
            floatValues[2 * size + idx] = b
        }
        return floatValues
    }
}
