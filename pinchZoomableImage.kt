package com.example.pinchzoom.submodule.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale


typealias PinchZoomImageLoader = @Composable (
    pinchZoomImageData : PunchZoomImageData
) -> Unit

data class PunchZoomImageData(
    val modifier            : Modifier          = Modifier,
    val model               : String            = "",
    val contentScale        : ContentScale      = ContentScale.Fit,
    val contentDescription  : String?           = null
)