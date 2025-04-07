package com.weit2nd.deepfakedetector.data.model

data class EmotionResult(
    val label: EmotionLabel,
    val probability: Float,
)
