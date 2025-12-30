package com.lamasatech.demo.mic

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield

/**
 * A cold Kotlin Flow that streams raw microphone audio bytes in real time.
 *
 * This class encapsulates audio recording via Android's [AudioRecord] API and exposes it as a Flow,
 * making it easy to consume microphone PCM 16-bit mono data reactively from a coroutine.
 *
 * The audio input stream is produced only when [collect] is invoked.
 * Each emission is a freshly read audio chunk of bytes (not shared), suitable for further processing,
 * streaming to a service, writing to a file, or real-time speech/voice analysis.
 *
 * The flow runs on the calling coroutine context and respects cancellation/suspension.
 *
 * Example usage:
 * ```
 * val audioInput = AudioInputFlow()
 * lifecycleScope.launch {
 *   audioInput.collect { bytes ->
 *     // process mic data for speech, liveness etc.
 *   }
 * }
 * ```
 *
 * @param sampleRate The sample rate in Hz (default: 16,000 Hz, suitable for speech)
 * @param channelConfig Channel config (default: [AudioFormat.CHANNEL_IN_MONO])
 * @param audioFormat Format for audio samples (default: [AudioFormat.ENCODING_PCM_16BIT])
 */
class AudioInputFlow(
    private val sampleRate: Int = 16_000,
    private val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
    private val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
) : Flow<ByteArray?> {

    /**
     * Minimum buffer size, calculated once from current settings.
     */
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        channelConfig,
        audioFormat
    )

    /**
     * Starts recording audio from the MIC and emits data as byte arrays to the given collector.
     * The flow will not emit if the [AudioRecord] initialization fails.
     * The recording stops and is cleaned up if the coroutine is cancelled or the flow completes.
     *
     * @param collector Receives each successfully read audio byte buffer.
     * @throws IllegalStateException if audio record initialization fails.
     */
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    override suspend fun collect(collector: FlowCollector<ByteArray?>) {
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            throw IllegalStateException("AudioRecord initialization failed")
        }

        audioRecord.startRecording()
        val buffer = ByteArray(bufferSize)

        try {
            while (currentCoroutineContext().isActive) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    // Emit a copy of the buffer of only the valid bytes
                    collector.emit(buffer.copyOf(read))
                } else {
                    // If read <= 0, yield to avoid blocking loop in case of spurious read errors
                    yield()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            audioRecord.stop()
            audioRecord.release()
        }
    }
}