package com.lamasatech.demo.camera

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FrameFlow {

    private val _latestPair = MutableStateFlow<DualFrame?>(null)
    val latestPair: StateFlow<DualFrame?> = _latestPair
    private var rgbBuffer: Frame? = null
    private var irBuffer: Frame? = null
    private var count = 0
    private var isProcessing = false

    fun addRgbFrame(frame: Frame) = synchronized(this) {
        rgbBuffer = frame
        tryEmitPair()
    }

    fun addIrFrame(frame: Frame) = synchronized(this) {
        irBuffer = frame
        tryEmitPair()
    }

    private fun tryEmitPair() {
        if (!isProcessing && rgbBuffer != null && irBuffer != null) {
            _latestPair.value = DualFrame(rgbBuffer!!, irBuffer!!, count++)
            rgbBuffer = null
            irBuffer = null
        }
    }

    fun startFrameProcess() {
        isProcessing = true
    }

    fun endFrameProcess() {
        isProcessing = false
    }

}