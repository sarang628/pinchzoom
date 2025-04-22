package com.sarang.torang.di.pinchzoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

const val minZoom = 0.5f
const val maxZoom = 2f

/**
 * 핀치줌 상태 처리
 * @param zoomState 핀치줌 상태값
 */
fun Modifier.pinchZoomOverlay(
    zoomState: PinchZoomState
): Modifier {
    return this
        .pointerInput(Unit) {
            coroutineScope {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        zoomState.accumulateZoom.value *= event.calculateZoom() // 줌 누적하기
                        zoomState.accumulateZoom.value =
                            zoomState.accumulateZoom.value.coerceIn(
                                minZoom,
                                maxZoom
                            ) // 줌 최대 최소값 설정
                        if (zoomState.isZooming) { // 줌이 1f 보다 크다면
                            zoomState.offset.value =
                                zoomState.offset.value.plus(event.calculatePan()) // 핀치 포인터 이동 값만큼 이미지를 움직이기
                        }
                    } while (event.changes.any { it.pressed })

                    launch { // 손을 때면 애니메이션으로 제자리 되돌리기
                        animateZoomReset(zoomState)
                    }
                }
            }
        }
        .onGloballyPositioned { coordinates ->
            val bounds = coordinates.boundsInWindow()
            zoomState.topLeftInWindow.value = bounds.topLeft
        }
}

suspend fun animateZoomReset(zoomState: PinchZoomState) {
    val animZoom = Animatable(zoomState.accumulateZoom.value)
    val animOffsetX = Animatable(zoomState.offset.value.x)
    val animOffsetY = Animatable(zoomState.offset.value.y)

    // 애니메이션 시작
    coroutineScope {
        launch {
            animZoom.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) {
                zoomState.accumulateZoom.value = value
            }
        }
        launch {
            animOffsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) {
                zoomState.offset.value = zoomState.offset.value.copy(x = value)
            }
        }
        launch {
            animOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) {
                zoomState.offset.value = zoomState.offset.value.copy(y = value)
            }
        }
    }
}

@Composable
fun Modifier.transFormByZoomState(zoomState: PinchZoomState): Modifier {
    return this.graphicsLayer {
        scaleX = zoomState.accumulateZoom.value
        scaleY = zoomState.accumulateZoom.value
        translationX = zoomState.offset.value.x
        translationY = zoomState.offset.value.y
    }
}

@Composable
fun Modifier.pinchZoomAndTransform(zoomState: PinchZoomState): Modifier {
    return this
        .pinchZoomOverlay(zoomState)
        .transFormByZoomState(zoomState)
}