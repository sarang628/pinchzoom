package com.example.pinchzoom.submodule.pinchzoom

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.layout.ContentScale
import com.sarang.torang.di.pinchzoom.ImageLoader
import com.sarang.torang.di.pinchzoom.PinchZoomState
import com.sarang.torang.di.pinchzoom.PinchZoomableImageType
import com.sarang.torang.di.pinchzoom.ZoomSnapshot
import com.sarang.torang.di.pinchzoom.pinchZoomAndTransform
import kotlinx.coroutines.flow.distinctUntilChanged

fun pinchZoomableImage(
    imageLoader: ImageLoader,
    onZoomState: (PinchZoomState) -> Unit = {}
): PinchZoomableImageType = { modifier, model, contentScale, originHeight ->
    val zoomState =
        remember {
            PinchZoomState(
                originHeight = originHeight?.value ?: 0f,
                url = model
            )
        }

    LaunchedEffect(zoomState) {
        snapshotFlow {
            ZoomSnapshot(
                zoomState.accumulateZoom.value,
                zoomState.offset.value,
                zoomState.isZooming.value
            )
        }.distinctUntilChanged()
            .collect {
                onZoomState(zoomState)
            }
    }


    imageLoader.invoke(
        modifier.pinchZoomAndTransform(zoomState),
        model,
        contentScale ?: ContentScale.Fit
    )
}