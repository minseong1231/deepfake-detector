package com.weit2nd.deepfakedetector.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit2nd.deepfakedetector.domain.pickimage.PickSingleImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val pickSingleImage: PickSingleImage,
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _event = Channel<MainEvent>(
        capacity = Channel.BUFFERED,
    )
    val event = _event.receiveAsFlow()

    fun onImageClick() {
        MainIntent.SelectImage.post()
    }

    private fun MainIntent.post() {
        viewModelScope.launch {
            when (this@post) {
                MainIntent.SelectImage -> {
                    val selectedImage = pickSingleImage()
                    _state.update {
                        it.copy(
                            imageUri = selectedImage,
                        )
                    }
                }
            }
        }
    }

    private fun sendEvent(event: MainEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
