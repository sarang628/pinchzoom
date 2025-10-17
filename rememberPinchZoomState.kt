package com.example.pinchzoom.library.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sarang.torang.di.pinchzoom.PinchZoomState

@Composable
fun rememberPinchZoomState(): PinchZoomState {
    return remember {
        PinchZoomState()
    }
}