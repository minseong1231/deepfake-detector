package com.weit2nd.deepfakedetector.data.repository.imageaesthetic

import com.weit2nd.deepfakedetector.R
import com.weit2nd.deepfakedetector.data.source.localimage.LocalImageDataSource
import com.weit2nd.deepfakedetector.data.source.model.OnnxDataSource
import com.weit2nd.deepfakedetector.data.util.softmax
import javax.inject.Inject

class ImageAestheticRepositoryImpl @Inject constructor(
    private val onnxDataSource: OnnxDataSource,
    private val localImageDataSource: LocalImageDataSource,
) : ImageAestheticRepository {
    // batch, channel, height, width
    private val defaultShape = longArrayOf(
        1, 3, DEFAULT_IMAGE_HEIGHT.toLong(), DEFAULT_IMAGE_WIDTH.toLong()
    )

    override suspend fun getAestheticScore(uri: String): Float {
        val session = onnxDataSource.getSession(
            modelIdRes = R.raw.model_aesthetic_q4f16
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
            logits.toScore()
        }
    }

    private fun FloatArray.toScore(): Float = this.softmax()[0] * 100f

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 1024
        private const val DEFAULT_IMAGE_HEIGHT = 1024
    }
}
