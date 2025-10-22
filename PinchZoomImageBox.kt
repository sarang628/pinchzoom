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
    modifier        : Modifier                  = Modifier,
    showLog         : Boolean                   = false,
    imageLoader     : ImageLoader,
    activeZoomState : PinchZoomState?           = null,
    content         : @Composable () -> Unit    = {}
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
    imageLoader     : ImageLoader,
    activeZoomState : PinchZoomState?           = null,
    imageSize       : Dp                        = 0.dp,
    showLog         : Boolean                   = false
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
                            model = state.url,
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

// pinch zoom custom image loader
fun pinchZoomImageLoader(
    zoomState   : PinchZoomState?,
    tag         : String = "__pinchZoomImageLoader",
    showLog     : Boolean = false,
    onZoomState : (PinchZoomState?)->Unit  ={}
) : PinchZoomImageLoader = @Composable { data ->
    AsyncImage(
        modifier = data.modifier
            .height(200.dp)
            .pinchZoomAndTransform(zoomState, onActiveZoom = {
                showLog.d(tag, "onAciveZoom : $data")
                onZoomState(it?.copy(url = data.model)) }
            ),
        model = data.model,
        contentDescription = data.contentDescription
    )
}

@Composable
fun PinchZoomImageBoxSample(modifier : Modifier = Modifier, showLog: Boolean = false){

    val tag = "__PinchZoomImageBoxSample"

    val imageUrls = listOf(
        "http://sarang628.iptime.org:89/restaurant_images/278/2025-10-12/07_53_33_425.jpg%3ftype=w800",
        "http://sarang628.iptime.org:89/restaurant_images/245/2025-10-12/01_18_37_646.jpg",
        "http://sarang628.iptime.org:89/restaurant_images/244/2025-08-23/11_46_30_054.jpg",
        "http://sarang628.iptime.org:89/restaurant_images/242/2025-05-03/02_34_45_987.jpeg",
        "http://sarang628.iptime.org:89/restaurant_images/241/2025-05-03/02_32_41_199.jpeg",
        "http://sarang628.iptime.org:89/restaurant_images/239/2025-05-03/02_30_21_802.jpg%3fw=500&h=500&org_if_sml=1",
        "http://sarang628.iptime.org:89/restaurant_images/237/2025-05-03/10_54_53_555.jpg",
        "http://sarang628.iptime.org:89/restaurant_images/236/2025-05-03/09_33_55_764.jpg"
    )

    // Data shared between a zoomed image and the rest of the list when zooming.
    var zoomState by remember { mutableStateOf<PinchZoomState?>(null) }

    showLog.d(tag, "recomposition")

    PinchZoomImageBox(
        modifier        = modifier,
        activeZoomState = zoomState,
        imageLoader     = imageLoader
    ){
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled   = remember(zoomState) { zoomState == null } // scrollEnabled는 derivedStateOf로 wrapping → recomposition 방지
        ) {
            items(imageUrls.size) {
                Column {
                    pinchZoomImageLoader(
                        zoomState   = zoomState,
                        showLog     =  true,
                        onZoomState = { zoomState = it }
                    ).invoke(
                        PunchZoomImageData(
                            model               = imageUrls[it],
                            contentDescription  = null
                        )
                    )
                }
            }
        }
    }
}

