package com.weit2nd.deepfakedetector.data.repository.imageaesthetic

import com.weit2nd.deepfakedetector.data.model.DeepFakeResult

interface ImageAestheticRepository {
    suspend fun getAestheticScore(uri: String): Float
}
