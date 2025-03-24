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
    // batch, channel, height, width
    private val defaultShape = longArrayOf(
        1, 3, DEFAULT_IMAGE_HEIGHT.toLong(), DEFAULT_IMAGE_WIDTH.toLong()
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
            width = DEFAULT_IMAGE_WIDTH,
            height = DEFAULT_IMAGE_HEIGHT,
        )
        val floatValues = FloatArray(3 * DEFAULT_IMAGE_SIZE)
        val pixels = IntArray(DEFAULT_IMAGE_SIZE)
        bitmap.getPixels(
            /* pixels = */ pixels,
            /* offset = */ 0,
            /* stride = */ DEFAULT_IMAGE_WIDTH,
            /* x = */ 0,
            /* y = */ 0,
            /* width = */ DEFAULT_IMAGE_WIDTH,
            /* height = */ DEFAULT_IMAGE_HEIGHT,
        )

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            val x = i % DEFAULT_IMAGE_WIDTH
            val y = i / DEFAULT_IMAGE_WIDTH
            val idx = y * DEFAULT_IMAGE_WIDTH + x

            floatValues[idx] = r
            floatValues[DEFAULT_IMAGE_SIZE + idx] = g
            floatValues[2 * DEFAULT_IMAGE_SIZE + idx] = b
        }
        return floatValues
    }

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 224
        private const val DEFAULT_IMAGE_HEIGHT = 224
        private const val DEFAULT_IMAGE_SIZE = DEFAULT_IMAGE_WIDTH * DEFAULT_IMAGE_HEIGHT
    }
}
