package com.sarang.torang.di.pinchzoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.pinchzoom.submodule.pinchzoom.pinchZoomableImage

/**
 * pinch zoom 확대 이미지를 보여주는 Box
 * @param imageLoader 이미지 compose(coil glide 원하는 라이브러리를 [ImageType]에 맞춰 구현)
 * @param contents 박스안에 들어갈 UI. [ZoomableTorangAsyncImage]와 [PinchZoomState]를 contents에 전달함.
 *
 * 주의! ZoomableImage를 사용하여 이미지를 로드해야 함. PinchZoomState로 이미지의 핀치줌 상태를 확인할 수 있음.
 */
@Composable
fun PinchZoomImageBox(
    imageLoader: ImageType,
    contents: @Composable (PinchZoomableImageType, PinchZoomState) -> Unit
) {
    var zoomState by remember { mutableStateOf(PinchZoomState()) } // Image 의 pinch 상태를 받기 위한 state
    Box(Modifier.fillMaxSize())
    {
        contents(
            pinchZoomableImage(imageLoader = imageLoader, onZoomState = { zoomState = it }) // ZoomableImage 를 contents 에 전달
            , zoomState // PinchZoomState 를 contents 에 전달
        )

        if (zoomState.isZooming.value) { // 줌 상태면 바깥 이미지 보여 주기
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.30f))
            ) {
                imageLoader.invoke( // 바깥 이미지
                    Modifier
                        .offset(zoomState.topLeftInWindow.value) // 줌 대상 이미지의 화면상 위치와 동일하게 맞추기
                        .height(zoomState.originHeight.dp) // 줌 대상 이미지 높이 동일하게 맞추기
                        .transFormByZoomState(zoomState), // 핀치줌 크기 적용
                    zoomState.url,
                    ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun Modifier.offset(offset: Offset): Modifier {
    val offsetX = with(LocalDensity.current) { offset.x.toDp() }
    val offsetY = with(LocalDensity.current) { offset.y.toDp() }
    return this.offset(offsetX, offsetY)
}


