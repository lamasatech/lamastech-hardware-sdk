package com.lamasatech.demo.camera

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Thread-safe synchronizer and distributor for RGB and IR camera frames.
 *
 * [FrameFlow] receives and holds the latest available RGB and IR [Frame]s. Once both are present,
 * it pairs them into a [DualFrame] and emits them via a [StateFlow].
 *
 * - All frame additions and emissions are synchronized.
 * - The processing lifecycle (to avoid overlapping frame processing) is controlled
 *   by [startFrameProcess] and [endFrameProcess].
 *
 * Typical usage: Camera providers call [addRgbFrame] and [addIrFrame]; downstream
 * consumers observe [latestPair] for up-to-date paired frames.
 */
class FrameFlow {

    private val _latestPair = MutableStateFlow<DualFrame?>(null)
    /**
     * Observed by consumers; emits the most recent available [DualFrame] or `null`.
     */
    val latestPair: StateFlow<DualFrame?> = _latestPair
    private var rgbBuffer: Frame? = null
    private var irBuffer: Frame? = null
    private var count = 0
    private var isProcessing = false

    /**
     * Receives a new RGB frame. If an IR frame is also ready and not being processed, emits a pair.
     * @param frame The RGB [Frame] from the camera.
     */
    fun addRgbFrame(frame: Frame) = synchronized(this) {
        rgbBuffer = frame
        tryEmitPair()
    }

    /**
     * Receives a new IR frame. If an RGB frame is also ready and not being processed, emits a pair.
     * @param frame The IR [Frame] from the camera.
     */
    fun addIrFrame(frame: Frame) = synchronized(this) {
        irBuffer = frame
        tryEmitPair()
    }

    /**
     * Checks if we currently have both an RGB and IR frame and are not processing. If so, emits [DualFrame].
     */
    private fun tryEmitPair() {
        if (!isProcessing && rgbBuffer != null && irBuffer != null) {
            _latestPair.value = DualFrame(rgbBuffer!!, irBuffer!!, count++)
            rgbBuffer = null
            irBuffer = null
        }
    }

    /**
     * Called by consumers to signal the start of frame processing (prevents new emissions).
     */
    fun startFrameProcess() {
        isProcessing = true
    }

    /**
     * Called by consumers to signal that processing is finished (enables new emissions).
     */
    fun endFrameProcess() {
        isProcessing = false
    }
}