package com.sarang.torang.di.pinchzoom

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pinchzoom.submodule.pinchzoom.PinchZoomImageLoader
import com.example.pinchzoom.submodule.pinchzoom.PunchZoomImageData

/**
 * ### pinch zoom 이미지를 포함한 Box Layout
 *
 * imageLoader에 [ImageLoader]에 맞춰 이미지 로드 컴포넌트 구현 필요.(coil, glide와 같은 라이브러리 사용 가능.)
 *
 * 주의! [ZoomableImage]를 사용하여 이미지를 로드해야 함. [PinchZoomState]로 이미지의 핀치줌 상태를 확인할 수 있음.
 *
 * @param imageLoader 이미지 로더 compose
 * @param contents 박스안에 들어갈 contents.
 */
@Composable
fun PinchZoomImageBox(
    modifier : Modifier = Modifier,
    imageLoader: ImageLoader,
    activeZoomState: PinchZoomState? = null,
    content : @Composable () -> Unit = {}
) {
    val tag = "__PinchZoomExample"
    val imageSize = 200.dp
    // ② rememberUpdatedState로 overlay scope만 최신값 반영
    val currentZoomState by rememberUpdatedState(activeZoomState)
    // ③ Log는 recomposition이 실제로 일어나는 곳에서만 확인
    LaunchedEffect(Unit) { Log.d(tag, "composition created once") }

    Box(modifier) {
        content()
        // ⑥ Overlay를 별도 함수로 분리 + rememberUpdatedState로 스코프 최소화
        OverlayImage(modifier = modifier, imageLoader = imageLoader, activeZoomState = currentZoomState, imageSize = imageSize)
    }
}

@Composable
fun OverlayImage(modifier : Modifier = Modifier, imageLoader: ImageLoader, activeZoomState: PinchZoomState? = null, imageSize : Dp) {
    activeZoomState?.let {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
        ) {
            imageLoader.invoke( // 바깥 이미지
                ImageData(
                    model = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhkYTY17vrtw3-dlooIu9n7R7mYFgOwyiCwEtJiFJTuxk4sOCKJ-OVaftSPKX7CfONCn2AMMV70TNP9qfo5avZBaMBn4BGS5DW6wPlbRY2ZZRgBXMEI5HbzduVdwj790uDattXfmQtkE8JJ_OptUUDFpCdJZWKVO_mOuL408H4svVQlt58TcjQe8JWfC5g/s1600/app-quality-performance-play-console-insights-meta.png",
                    contentDescription = null,
                    modifier = Modifier
                        .offset(it.topLeftInWindow.value)
                        .height(imageSize)
                        .transFormByZoomState(it)
                )
            )
        }
    }
}

@Composable
private fun Modifier.offset(offset: Offset): Modifier {
    val offsetX = with(LocalDensity.current) { offset.x.toDp() }
    val offsetY = with(LocalDensity.current) { offset.y.toDp() }
    return this.offset(offsetX, offsetY)
}


@Composable
fun PinchZoomImageBoxSample(modifier : Modifier){

    var activeZoomState by remember { mutableStateOf<PinchZoomState?>(null) }

    // emit active zoom
    val pinchZoomImageLoader : PinchZoomImageLoader = @Composable {
        AsyncImage(
            modifier = it.modifier
                .height(200.dp)
                .pinchZoomAndTransform {
                    activeZoomState = it
                },
            model = it.model,
            contentDescription = it.contentDescription
        )
    }

    PinchZoomImageBox(
        modifier = modifier,
        activeZoomState = activeZoomState,
        imageLoader = imageLoader
    ){
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            // ④ scrollEnabled는 derivedStateOf로 wrapping → recomposition 방지
            userScrollEnabled = remember(activeZoomState) { activeZoomState == null }
        ) {
            items(10) {
                Column {
                    pinchZoomImageLoader(
                        PunchZoomImageData(
                            model = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhkYTY17vrtw3-dlooIu9n7R7mYFgOwyiCwEtJiFJTuxk4sOCKJ-OVaftSPKX7CfONCn2AMMV70TNP9qfo5avZBaMBn4BGS5DW6wPlbRY2ZZRgBXMEI5HbzduVdwj790uDattXfmQtkE8JJ_OptUUDFpCdJZWKVO_mOuL408H4svVQlt58TcjQe8JWfC5g/s1600/app-quality-performance-play-console-insights-meta.png",
                            contentDescription = null
                        )
                    )
                }
            }
        }
    }
}

