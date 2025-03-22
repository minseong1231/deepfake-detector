package com.weit2nd.deepfakedetector.data.repository.pickimage

import com.weit2nd.deepfakedetector.data.source.pickimage.PickImageDataSource
import javax.inject.Inject

class PickImageRepositoryImpl @Inject constructor(
    private val pickImageDataSource: PickImageDataSource,
) : PickImageRepository {
    override suspend fun pickImage(): String? {
        return pickImageDataSource.pickImage()?.toString()
    }
}
