package com.weit2nd.deepfakedetector.domain.deepfake

import com.weit2nd.deepfakedetector.data.model.DeepFakeResult
import com.weit2nd.deepfakedetector.data.repository.deepfake.DeepFakeDetectorRepository
import javax.inject.Inject

class DetectDeepFakeImage @Inject constructor(
    private val repository: DeepFakeDetectorRepository,
) {
    /**
     * 이미지가 DeepFake인지 아닌지 판별합니다.
     */
    suspend operator fun invoke(uri: String): DeepFakeResult {
        return repository.detectDeepFakeImage(uri)
    }
}
