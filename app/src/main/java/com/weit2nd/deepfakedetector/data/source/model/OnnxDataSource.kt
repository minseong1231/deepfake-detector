package com.weit2nd.deepfakedetector.data.source.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import androidx.annotation.RawRes
import com.weit2nd.deepfakedetector.data.util.TokenizerBridge
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.LongBuffer
import javax.inject.Inject

class OnnxDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mutex = Mutex()
    private var session: OrtSession? = null
    private val sessionInitDispatcher = Dispatchers.IO.limitedParallelism(1)
    private val ortEnvironment = OrtEnvironment.getEnvironment()

    suspend fun getSession(
        @RawRes modelIdRes: Int,
    ): OrtSession = withContext(sessionInitDispatcher) {
        mutex.withLock {
            session ?: run {
                val newSession = createSession(
                    modelIdRes = modelIdRes,
                )
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

    suspend fun createTenser(
        inputTensor: LongArray,
        shape: LongArray,
    ) = withContext(Dispatchers.IO) {
        OnnxTensor.createTensor(
            /* env = */ ortEnvironment,
            /* data = */ LongBuffer.wrap(inputTensor),
            /* shape = */ shape,
        )
    }



    suspend fun createToken(
        @RawRes tokenizerJsonRes: Int,
        sentence: String,
    ): LongArray = withContext(Dispatchers.IO) {
        val json = context.resources
            .openRawResource(tokenizerJsonRes)
            .bufferedReader()
            .use { it.readText() }
        TokenizerBridge.tokenize(
            clipTokenizerJson = json,
            input = sentence,
        )
    }

    private fun createSession(
        @RawRes modelIdRes: Int,
    ): OrtSession {
        return context.resources.openRawResource(modelIdRes).use { modelStream ->
            val size = modelStream.available()
            val buffer = ByteBuffer.allocateDirect(size)
            val bytes = ByteArray(8192)
            var read: Int
            while (modelStream.read(bytes).also { read = it } != -1) {
                buffer.put(bytes, 0, read)
            }
            buffer.rewind()
            ortEnvironment.createSession(buffer)
        }
    }
}
