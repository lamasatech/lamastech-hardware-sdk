package com.lamasatech.demo.camera

/**
 * Functional contract for receiving and processing [DualFrame]s.
 *
 * This interface allows consumers to implement suspend functions that run
 * on each paired RGB/IR frame—potentially for real-time face detection, liveness detection,
 * video streaming, analytics, or other multimedia tasks.
 *
 * Typical implementations:
 * ```
 * class MyProcessor : FrameProcessor {
 *     override suspend fun process(frame: DualFrame) {
 *         // Analyze or forward the frames here
 *     }
 * }
 * ```
 */
interface  FrameProcessor {
    /**
     * Called for every available [DualFrame]; implement with suspend logic as appropriate.
     * This will be called on a background coroutine dispatcher.
     *
     * @param frame Synchronized RGB and IR frames, plus a sequence number.
     */
    suspend fun process(frame: DualFrame)
}