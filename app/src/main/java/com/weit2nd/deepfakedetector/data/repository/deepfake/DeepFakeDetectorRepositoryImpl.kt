package com.weit2nd.deepfakedetector.data.repository.deepfake

import com.weit2nd.deepfakedetector.R
import com.weit2nd.deepfakedetector.data.model.DeepFakeResult
import com.weit2nd.deepfakedetector.data.source.localimage.LocalImageDataSource
import com.weit2nd.deepfakedetector.data.source.model.OnnxDataSource
import com.weit2nd.deepfakedetector.data.util.softmax
import javax.inject.Inject

class DeepFakeDetectorRepositoryImpl @Inject constructor(
    private val onnxDataSource: OnnxDataSource,
    private val localImageDataSource: LocalImageDataSource,
) : DeepFakeDetectorRepository {
    // batch, channel, height, width
    private val defaultShape = longArrayOf(
        1, 3, DEFAULT_IMAGE_HEIGHT.toLong(), DEFAULT_IMAGE_WIDTH.toLong()
    )

    override suspend fun detectDeepFakeImage(uri: String): DeepFakeResult {
        val session = onnxDataSource.getSession(
            modelIdRes = R.raw.model_dima
        )
        val tensor = onnxDataSource.createTenser(
            inputTensor = preprocessBitmapToCHW(uri),
            shape = defaultShape,
        )
        val inputMap = mapOf(session.inputNames.first() to tensor)
        return session.run(inputMap).use {
            val logits = (it[0].value as Array<FloatArray>)[0]
            logits.toDeepFakeResult()
        }
    }

    private fun FloatArray.toDeepFakeResult(): DeepFakeResult {
        val softmax = this.softmax()
        return DeepFakeResult(
            deepFake = softmax[1],
            real = softmax[0],
        )
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
