package com.weit2nd.deepfakedetector.data.repository.deepfake

import android.util.Log
import com.weit2nd.deepfakedetector.data.model.DeepFakeResult
import com.weit2nd.deepfakedetector.data.source.deepfake.DeepFakeDetectorDataSource
import com.weit2nd.deepfakedetector.data.source.localimage.LocalImageDataSource
import javax.inject.Inject

class DeepFakeDetectorRepositoryImpl @Inject constructor(
    private val deepFakeDetectorDataSource: DeepFakeDetectorDataSource,
    private val localImageDataSource: LocalImageDataSource,
) : DeepFakeDetectorRepository {
    private val defaultShape = longArrayOf(
        1, 3, DEFAULT_IMAGE_SIZE.toLong(), DEFAULT_IMAGE_SIZE.toLong()
    )

    override suspend fun detectDeepFakeImage(uri: String): DeepFakeResult {
        val session = deepFakeDetectorDataSource.getSession()
        val tensor = deepFakeDetectorDataSource.createTenser(
            inputTensor = preprocessBitmapToCHW(uri),
            shape = defaultShape,
        )
        return session.run(mapOf(session.inputNames.first() to tensor)).use {
            (it[0].value as Array<FloatArray>)[0].toDeepFakeResult()
        }
    }

    private fun FloatArray.toDeepFakeResult(): DeepFakeResult {
        val softmax = softmax(this)
        return DeepFakeResult(
            deepFake = softmax[0],
            real = softmax[1],
        )
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0f
        val exps = logits.map { Math.exp((it - maxLogit).toDouble()) }
        val sumExp = exps.sum()
        return exps.map {
            (it / sumExp).toFloat()
        }.toFloatArray()
    }

    private suspend fun preprocessBitmapToCHW(uri: String): FloatArray {
        val bitmap = localImageDataSource.getResizedBitmap(
            uri = uri,
            width = DEFAULT_IMAGE_SIZE,
            height = DEFAULT_IMAGE_SIZE,
        )
        val width = bitmap.width
        val height = bitmap.height
        val floatValues = FloatArray(3 * width * height)
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            val x = i % width
            val y = i / width
            val idx = y * width + x

            floatValues[idx] = r
            floatValues[1 * width * height + idx] = g
            floatValues[2 * width * height + idx] = b
        }
        return floatValues
    }

    companion object {
        private const val DEFAULT_IMAGE_SIZE = 224
    }
}
