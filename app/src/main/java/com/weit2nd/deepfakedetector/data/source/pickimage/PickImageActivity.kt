package com.weit2nd.deepfakedetector.data.source.pickimage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PickImageActivity : AppCompatActivity() {
    @Inject
    lateinit var pickImageDataSource: PickImageDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { image ->
                image?.let {
                    takeUriPermission(it)
                }
                sendSelectedImageAndFinish(image)
            }
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun takeUriPermission(image: Uri) {
        contentResolver.takePersistableUriPermission(image, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    private fun sendSelectedImageAndFinish(image: Uri?) {
        lifecycleScope.launch {
            pickImageDataSource.emitImage(image)
        }.invokeOnCompletion {
            finish()
        }
    }
}
