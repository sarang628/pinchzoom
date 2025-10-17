package com.sarang.torang.di.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * 이미지 Type
 * 핀치줌에 사용 이미지 로더 interface
 */
typealias ImageLoader = @Composable (
    imageData : ImageData
) -> Unit

data class ImageData(
    val modifier            : Modifier       = Modifier,
    val model               : String         = "",
    val contentScale        : ContentScale   = ContentScale.Fit,
    val contentDescription  : String?        = null,
)

// outside image
val imageLoader : ImageLoader = @Composable {
    AsyncImage(
        modifier = it.modifier,
        model = it.model,
        contentDescription = it.contentDescription
    )
}