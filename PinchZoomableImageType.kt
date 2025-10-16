package com.sarang.torang.di.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 핀치줌 이미지 type
 * @param modifier
 * @param url
 * @param contentScale
 * @param originHeight
 */
typealias PinchZoomableImageType = @Composable (
    pinchZoomableImageData : PinchZoomableImageData
) -> Unit


data class PinchZoomableImageData(
    val modifier: Modifier = Modifier,
    val url: String = "",
    val contentScale: ContentScale = ContentScale.Fit,
    val originHeight: Dp = 0.dp
)