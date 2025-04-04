package com.weit2nd.deepfakedetector.domain.emotion

import com.weit2nd.deepfakedetector.data.repository.emotion.EmotionRepository
import javax.inject.Inject

class GetEmotion @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(sentence: String): String {
        return repository.getEmotion(sentence)
    }
}
