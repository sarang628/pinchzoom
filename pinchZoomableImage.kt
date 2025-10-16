package com.example.pinchzoom.submodule.pinchzoom

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.layout.ContentScale
import com.sarang.torang.di.pinchzoom.ImageData
import com.sarang.torang.di.pinchzoom.ImageLoader
import com.sarang.torang.di.pinchzoom.PinchZoomState
import com.sarang.torang.di.pinchzoom.PinchZoomableImageType
import com.sarang.torang.di.pinchzoom.ZoomSnapshot
import com.sarang.torang.di.pinchzoom.isZooming
import com.sarang.torang.di.pinchzoom.pinchZoomAndTransform
import kotlinx.coroutines.flow.distinctUntilChanged

fun pinchZoomableImage(
    imageLoader: ImageLoader,
    onZoomState: (PinchZoomState) -> Unit = {}
): PinchZoomableImageType = { it ->
    val zoomState =
        remember {
            PinchZoomState(
                originHeight = it.originHeight.value,
                url = it.url
            )
        }

    LaunchedEffect(zoomState) {
        snapshotFlow {
            ZoomSnapshot(
                zoomState.accumulateZoom.value,
                zoomState.offset.value,
                zoomState.isZooming
            )
        }.distinctUntilChanged()
            .collect {
                onZoomState(zoomState)
            }
    }


    imageLoader.invoke(
        ImageData(
            modifier = it.modifier.pinchZoomAndTransform(zoomState),
            url = it.url,
            contentScale = it.contentScale
        )
    )
}