package com.weit2nd.deepfakedetector.domain.imageaesthitic

import com.weit2nd.deepfakedetector.data.repository.imageaesthetic.ImageAestheticRepository
import javax.inject.Inject

class GetAestheticScore @Inject constructor(
    private val repository: ImageAestheticRepository,
) {
    suspend operator fun invoke(uri: String): Float {
        return repository.getAestheticScore(uri)
    }
}
