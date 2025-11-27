package com.sarang.torang.di.pinchzoom

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pinchzoom.submodule.pinchzoom.PinchZoomImageLoader
import com.example.pinchzoom.submodule.pinchzoom.PinchZoomImageData

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
    tag             : String                    = "__PinchZoomImageBox",
    showLog         : Boolean                   = false,
    imageLoader     : ImageLoader,
    activeZoomState : PinchZoomState?           = null,
    content         : @Composable () -> Unit    = { Log.i(tag, "content does not set") }
) {
    val tag = "__PinchZoomExample"
    // ② rememberUpdatedState로 overlay scope만 최신값 반영
    val currentZoomState by rememberUpdatedState(activeZoomState)
    // ③ Log는 recomposition이 실제로 일어나는 곳에서만 확인
    LaunchedEffect(Unit) { showLog.d(tag, "composition created once") }

    Box(modifier) {
        content()
        // ⑥ Overlay를 별도 함수로 분리 + rememberUpdatedState로 스코프 최소화
        OverlayImage(
            imageLoader = imageLoader,
            activeZoomState = currentZoomState,
            showLog = showLog
        )
    }
}

@Composable
fun OverlayImage(
    tag             : String                    = "__OverlayImage",
    imageLoader     : ImageLoader,
    activeZoomState : PinchZoomState?           = null,
    showLog         : Boolean                   = false
) {
    activeZoomState?.let {
        //TODO:: innerpadding 보정 어떻게 계산하는지 분석
        var parentCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) } // scaffold inner padding 보정

        // offset 변화 로그 찍기
        // 3개 상태를 한 번에 감지하는 Flow
        LaunchedEffect(activeZoomState) {
            snapshotFlow {
                Triple(
                    activeZoomState.offset.value,
                    activeZoomState.accumulateZoom.value,
                    activeZoomState.topLeftInWindow.value
                )
            }.collect { (offset, zoom, topLeft) ->
                    showLog.d(
                        tag,
                        """
                        🔍 PinchZoomState changed: height = ${it.originHeight} offset = $offset zoom = $zoom topLeftInWindow = $topLeft
                        """.trimIndent()
                    )
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .onGloballyPositioned { parentCoordinates = it } // 부모 Box 좌표
        ) {
                //TODO:: innerpadding 보정 어떻게 계산하는지 분석
                parentCoordinates?.windowToLocal(it.topLeftInWindow.value)?.let { localOffset ->
                    imageLoader.invoke( // 바깥 이미지
                        ImageData(
                            model = it.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .offset(localOffset)
                                .fillMaxWidth()
                                .height(it.originHeight)
                                .transFormByZoomState(it)
                        )
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

// pinch zoom custom image loader
fun pinchZoomImageLoader(
    zoomState   : PinchZoomState?,
    tag         : String = "__pinchZoomImageLoader",
    showLog     : Boolean = false,
    onZoomState : (PinchZoomState?)->Unit  ={}
) : PinchZoomImageLoader = @Composable { data ->
    AsyncImage(
        modifier = data.modifier
            .pinchZoomAndTransform(zoomState, onActiveZoom = {
                if (it != null)
                    showLog.d(
                        tag,
                        "onAciveZoom : leftTop : ${it.topLeftInWindow.value}, height: ${it.originHeight}"
                    )
                onZoomState(it?.copy(url = data.model))
            }
            )
        ,
        model = data.model,
        contentDescription = data.contentDescription,
        contentScale = data.contentScale
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
        imageLoader     = imageLoader,
        showLog         = showLog
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
                        onZoomState = {
                            showLog.d(tag, "onZoomState $it")
                            zoomState = it
                        }
                    ).invoke(
                        PinchZoomImageData(
                            model               = imageUrls[it],
                            contentDescription  = null
                        )
                    )
                }
            }
        }
    }
}

