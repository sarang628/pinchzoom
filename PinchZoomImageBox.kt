package com.sarang.torang.di.pinchzoom

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pinchzoom.submodule.pinchzoom.PinchZoomImageLoader
import com.example.pinchzoom.submodule.pinchzoom.PunchZoomImageData

/**
 * ### pinch zoom 이미지를 포함한 Box Layout
 *
 * @param imageLoader [ImageLoader] type 에 맞춘 이미지 로더 컴포넌트 필요.(coil, glide와 같은 라이브러리 사용 가능.)
 * @param activeZoomState 현재 줌 액션 중인 이미지 상태
 * @param content 박스안에 들어갈 contents.
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
        OverlayImage(
            imageLoader = imageLoader,
            activeZoomState = currentZoomState,
            imageSize = imageSize
        )
    }
}

@Composable
fun OverlayImage(
    imageLoader: ImageLoader, activeZoomState: PinchZoomState? = null,
    imageSize: Dp
) {
    activeZoomState?.let {

        //TODO:: innerpadding 보정 어떻게 계산하는지 분석
        var parentCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) } // scaffold inner padding 보정

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .onGloballyPositioned { parentCoordinates = it } // 부모 Box 좌표
        ) {
            activeZoomState.let { state ->
                //TODO:: innerpadding 보정 어떻게 계산하는지 분석
                parentCoordinates?.windowToLocal(state.topLeftInWindow.value)?.let { localOffset ->
                    imageLoader.invoke( // 바깥 이미지
                        ImageData(
                            model = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhkYTY17vrtw3-dlooIu9n7R7mYFgOwyiCwEtJiFJTuxk4sOCKJ-OVaftSPKX7CfONCn2AMMV70TNP9qfo5avZBaMBn4BGS5DW6wPlbRY2ZZRgBXMEI5HbzduVdwj790uDattXfmQtkE8JJ_OptUUDFpCdJZWKVO_mOuL408H4svVQlt58TcjQe8JWfC5g/s1600/app-quality-performance-play-console-insights-meta.png",
                            contentDescription = null,
                            modifier = Modifier
                                .offset(localOffset)
                                .height(imageSize)
                                .transFormByZoomState(it)
                        )
                    )
                }
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


@Composable
fun PinchZoomImageBoxSample(modifier : Modifier = Modifier){

    var activeZoomState by remember { mutableStateOf<PinchZoomState?>(null) }

    Log.d("__PinchZoomImageBoxSample", "recomposition")

    // emit active zoom
    val pinchZoomImageLoader : PinchZoomImageLoader = @Composable {
        AsyncImage(
            modifier = it.modifier
                .height(200.dp)
                .pinchZoomAndTransform(activeZoomState) {
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

