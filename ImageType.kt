package com.sarang.torang.di.pinchzoom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * 이미지 Type
 * 핀치줌에 사용 이미지 로더 interface
 */
typealias ImageLoader = @Composable (
    modifier: Modifier,
    url: String,
    contentScale: ContentScale?
) -> Unit

val LocalImageLoader = compositionLocalOf<ImageLoader>{
    @Composable { modifier, url, contentScale ->

    }
}