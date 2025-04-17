package com.sarang.torang.di.pinchzoom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

/**
 * 핀치줌 이미지 type
 * @param modifier
 * @param url
 * @param contentScale
 * @param originHeight
 */
typealias PinchZoomableImageType = @Composable (
    modifier: Modifier,
    url: String,
    contentScale: ContentScale?,
    originHeight: Dp?
) -> Unit
