package com.weit2nd.deepfakedetector.domain.emotion

import com.weit2nd.deepfakedetector.data.model.EmotionResult
import com.weit2nd.deepfakedetector.data.repository.emotion.EmotionRepository
import javax.inject.Inject

class GetEmotion @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(sentence: String): List<EmotionResult> {
        return repository.getEmotion(sentence)
    }
}
