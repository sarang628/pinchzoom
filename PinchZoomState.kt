package com.sarang.torang.di.pinchzoom

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset

/**
 * 줌 상태
 * @param topLeftInWindow 줌 대상 원본 이미지의 현재 화면상 위치
 * @param accumulateZoom 핀치줌 누적 값
 * @param offset 이미지의 이동 거리 (핀치 줌 또는 드래그에 의해 이동된 오프셋)
 * @param url 줌 하고있는 이미지의 url
 * @param originHeight 줌 이미지의 화면상 원래 크기
 */
data class PinchZoomState(
    val topLeftInWindow: MutableState<Offset> = mutableStateOf(Offset(0f, 0f)),
    val accumulateZoom: MutableState<Float> = mutableFloatStateOf(1f),
    val offset: MutableState<Offset> = mutableStateOf(Offset(0f, 0f)),
    val url: String = "",
    val originHeight: Float = 0f
) {
    fun update(state: PinchZoomState) {
        topLeftInWindow.value = state.topLeftInWindow.value
        accumulateZoom.value = state.accumulateZoom.value
        offset.value = state.offset.value
    }
}

/**
 * @param isZooming 줌 동작중 여부
 */
val PinchZoomState.isZooming: Boolean get() = accumulateZoom.value != 1f