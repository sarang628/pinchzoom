package com.sarang.torang.di.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

typealias PinchZoomableImageType = @Composable (
    modifier: Modifier,
    text: String,
    contentScale: ContentScale?,
    originHeight: Dp?
) -> Unit
