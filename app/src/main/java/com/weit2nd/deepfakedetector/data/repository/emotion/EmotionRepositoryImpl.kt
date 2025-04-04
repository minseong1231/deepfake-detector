package com.weit2nd.deepfakedetector.data.repository.emotion

import ai.onnxruntime.OnnxTensor
import com.weit2nd.deepfakedetector.R
import com.weit2nd.deepfakedetector.data.source.model.OnnxDataSource
import com.weit2nd.deepfakedetector.data.util.softmax
import java.nio.LongBuffer
import javax.inject.Inject

class EmotionRepositoryImpl @Inject constructor(
    private val onnxDataSource: OnnxDataSource,
) : EmotionRepository {
    override suspend fun getEmotion(sentence: String): String {
        val session = onnxDataSource.getSession(
            modelIdRes = R.raw.model_emotion
        )
        val tokens = onnxDataSource.createToken(
            tokenizerJsonRes = R.raw.tokenizer_emotion,
            sentence = sentence
        )
        val paddedTokens = tokens + List(MAX_TOKEN_SIZE - tokens.size) { PAD_TOKEN_ID }
        val inputShape = longArrayOf(1, paddedTokens.size.toLong())
        val inputTensor = onnxDataSource.createTenser(
            inputTensor = paddedTokens,
            shape = inputShape,
        )
        val attentionTensor = createAttentionTensor(
            tokens = paddedTokens,
            shape = inputShape,
        )

        val inputMap = mapOf(
            "input_ids" to inputTensor,
            "attention_mask" to attentionTensor,
        )
        val result = session.run(inputMap)
        val output = (result[0].value as Array<FloatArray>).first().softmax()
        // TODO: 4/4/25 (minseong1231) 결과를 예쁘게 후처리
        return ""
    }

    private suspend fun createAttentionTensor(
        tokens: LongArray,
        shape: LongArray,
    ): OnnxTensor {
        val attentionMaskBuffer = LongBuffer.allocate(tokens.size)
        tokens.forEach {
            val mask = if (it == PAD_TOKEN_ID) {
                0L
            } else {
                1L
            }
            attentionMaskBuffer.put(mask)
        }
        attentionMaskBuffer.rewind()
        return onnxDataSource.createTenser(
            inputTensor = attentionMaskBuffer.array(),
            shape = shape,
        )
    }

    companion object {
        private const val MAX_TOKEN_SIZE = 512
        private const val PAD_TOKEN_ID = 1L
    }
}
