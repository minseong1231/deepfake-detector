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
import java.io.File
import java.io.FileOutputStream
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
    private var loadedModelId: Int? = null

    suspend fun getSession(
        @RawRes modelIdRes: Int,
    ): OrtSession = withContext(sessionInitDispatcher) {
        mutex.withLock {
            if (modelIdRes != loadedModelId) {
                session = null
                loadedModelId = null
            }
            session ?: run {
                val newSession = createSession(
                    modelIdRes = modelIdRes,
                )
                session = newSession
                loadedModelId = modelIdRes
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
        val file = File(context.cacheDir, "model.onnx")
        if (file.exists()) {
            file.delete()
        }
        val tempFile = File.createTempFile("model", ".onnx", context.cacheDir)
        context.resources.openRawResource(modelIdRes).use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return ortEnvironment.createSession(tempFile.absolutePath)
    }
}
