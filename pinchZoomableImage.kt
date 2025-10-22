package com.example.pinchzoom.submodule.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


typealias PinchZoomImageLoader = @Composable (
    pinchZoomImageData : PinchZoomImageData
) -> Unit

data class PinchZoomImageData(
    val modifier            : Modifier          = Modifier,
    val model               : String            = "",
    val contentScale        : ContentScale      = ContentScale.Fit,
    val contentDescription  : String?           = null,
    val height              : Dp                = 0.dp
)