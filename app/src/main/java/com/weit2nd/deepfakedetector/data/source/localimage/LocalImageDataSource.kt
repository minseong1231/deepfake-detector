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
}
