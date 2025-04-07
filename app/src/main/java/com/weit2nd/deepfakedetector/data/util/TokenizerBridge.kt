package com.weit2nd.deepfakedetector.data.util

object TokenizerBridge {
    init {
        System.loadLibrary("clip_tokenizer")
    }

    external fun tokenize(clipTokenizerJson: String, input: String): LongArray
}
