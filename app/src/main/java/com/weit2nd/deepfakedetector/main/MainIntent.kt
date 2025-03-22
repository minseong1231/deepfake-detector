package com.weit2nd.deepfakedetector.main

sealed interface MainIntent {
    data object SelectImage : MainIntent
}
