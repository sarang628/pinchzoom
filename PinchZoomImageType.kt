package com.sarang.torang.di.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

typealias PinchZoomImageType = @Composable (
    modifier: Modifier,
    url: String,
    contentScale: ContentScale?
) -> Unit