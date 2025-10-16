package com.sarang.torang.di.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

/**
 * 이미지 Type
 * 핀치줌에 사용 이미지 로더 interface
 */
typealias ImageLoader = @Composable (
    imageData : ImageData
) -> Unit

data class ImageData(
    val modifier : Modifier = Modifier,
    val url : String = "",
    val contentScale: ContentScale = ContentScale.Fit
)