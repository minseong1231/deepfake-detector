package com.weit2nd.deepfakedetector.data.repository.emotion

import com.weit2nd.deepfakedetector.data.model.EmotionResult

interface EmotionRepository {
    suspend fun getEmotion(
        sentence: String,
    ): List<EmotionResult>
}
