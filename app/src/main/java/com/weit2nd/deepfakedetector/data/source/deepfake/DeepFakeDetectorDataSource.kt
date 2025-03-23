package com.weit2nd.deepfakedetector.data.source.deepfake

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import com.weit2nd.deepfakedetector.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.FloatBuffer
import javax.inject.Inject

class DeepFakeDetectorDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mutex = Mutex()
    private var session: OrtSession? = null
    private val sessionInitDispatcher = Dispatchers.IO.limitedParallelism(1)
    private val ortEnvironment = OrtEnvironment.getEnvironment()

    suspend fun getSession(): OrtSession = withContext(sessionInitDispatcher) {
        mutex.withLock {
            session ?: run {
                val newSession = createSession()
                session = newSession
                newSession
            }
        }
    }

    suspend fun createTenser(
        inputTensor: FloatArray,
        shape: LongArray,
    ) = withContext(Dispatchers.IO) {
        OnnxTensor.createTensor(
            /* env = */ ortEnvironment,
            /* data = */ FloatBuffer.wrap(inputTensor),
            /* shape = */ shape,
        )
    }

    private fun createSession(): OrtSession {
        return context.resources.openRawResource(R.raw.model_fp16).use { modelStream ->
            val modelBytes = modelStream.readBytes()
            ortEnvironment.createSession(modelBytes)
        }
    }
}
