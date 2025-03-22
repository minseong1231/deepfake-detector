package com.weit2nd.deepfakedetector.data.repository.pickimage

interface PickImageRepository {
    suspend fun pickImage(): String?
}
