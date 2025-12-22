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

class AudioInputFlow(
    private val sampleRate: Int = 16_000,
    private val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
    private val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
) : Flow<ByteArray?> {

    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        channelConfig,
        audioFormat
    )

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
                    collector.emit(buffer.copyOf(read))
                } else {
                    // If read <= 0, yield to avoid blocking loop
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
