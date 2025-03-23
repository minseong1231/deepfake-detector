package com.weit2nd.deepfakedetector.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.weit2nd.deepfakedetector.R

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
    }

    Column(
        modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable {
                    viewModel.onImageClick()
                },
            model = state.imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            fallback = painterResource(id = R.drawable.ic_launcher_foreground),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            }
            if (state.isResultVisible) {
                ResultText(
                    deepFakePossibility = state.deepFakePossibility,
                    realPossibility = state.realPossibility,
                )
            }
        }
    }
}

@Composable
fun ResultText(
    deepFakePossibility: Float,
    realPossibility: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.main_deep_fake_possibility, deepFakePossibility),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.main_real_possibility, realPossibility),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
    }
}

private fun handleEvent(event: MainEvent) {
    when (event) {
        else -> {}
    }
}
