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
            inputTensor = localImageDataSource.preprocessBitmapToCHW(
                uri = uri,
                width = DEFAULT_IMAGE_WIDTH,
                height = DEFAULT_IMAGE_HEIGHT,
            ),
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

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 224
        private const val DEFAULT_IMAGE_HEIGHT = 224
    }
}
