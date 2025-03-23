package com.weit2nd.deepfakedetector.data.repository.deepfake

import com.weit2nd.deepfakedetector.data.model.DeepFakeResult

interface DeepFakeDetectorRepository {
    suspend fun detectDeepFakeImage(uri: String): DeepFakeResult
}
