package com.weit2nd.deepfakedetector.main

data class MainState(
    val imageUri: String? = null,
    val isLoading: Boolean = false,
    val isResultVisible: Boolean = false,
    val deepFakePossibility: Float = 0f,
    val realPossibility: Float = 0f,
)
