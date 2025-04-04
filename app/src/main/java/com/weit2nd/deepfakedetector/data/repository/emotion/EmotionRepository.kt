package com.weit2nd.deepfakedetector.data.repository.emotion

interface EmotionRepository {
    suspend fun getEmotion(
        sentence: String,
    ): String
}
