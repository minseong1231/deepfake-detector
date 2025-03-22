package com.weit2nd.deepfakedetector.data.source.pickimage

import android.content.Intent
import android.net.Uri
import com.weit2nd.deepfakedetector.data.util.ActivityProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PickImageDataSource @Inject constructor(
    private val activityProvider: ActivityProvider,
) {
    private val selectImageEvent = MutableSharedFlow<Uri?>()

    suspend fun pickImage(): Uri? {
        startPickImage()
        return selectImageEvent.first()
    }

    suspend fun emitImage(image: Uri?) {
        selectImageEvent.emit(image)
    }

    private fun startPickImage() {
        val intent = Intent(
            activityProvider.currentActivity,
            PickImageActivity::class.java,
        )
        activityProvider.currentActivity?.startActivity(intent)
    }
}
