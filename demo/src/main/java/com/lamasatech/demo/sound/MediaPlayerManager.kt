package com.lamasatech.demo.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.CoroutineContext

enum class PlayerState {
    IDLE, PREPARING, PLAYING, PAUSED, COMPLETED, ERROR
}

class MediaPlayerManager(
    private val context: Context,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
    private val prepareRetries: Int = 3,
    private val retryDelayMs: Long = 500L
) {

    private var mediaPlayer: MediaPlayer? = null
    private var state: PlayerState = PlayerState.IDLE
    private val scope = CoroutineScope(coroutineContext + SupervisorJob())
    private var updateJob: Job? = null

    /** Prepare media safely with retries (no nested functions) */
    fun prepare(
        url: String,
        autoplay: Boolean = false,
        onPrepared: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        release() // release existing player
        state = PlayerState.PREPARING

        var attempt = 0
        while (attempt < prepareRetries) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(url)
                    setOnPreparedListener {
                        state = PlayerState.PAUSED
                        onPrepared?.invoke()
                        if (autoplay) play()
                    }
                    setOnCompletionListener { state = PlayerState.COMPLETED }
                    setOnErrorListener { _, what, extra ->
                        state = PlayerState.ERROR
                        onError?.invoke(Exception("MediaPlayer error: what=$what extra=$extra"))
                        true
                    }
                    prepareAsync()
                }
                return // success, exit loop
            } catch (e: Exception) {
                attempt++
                if (attempt >= prepareRetries) {
                    state = PlayerState.ERROR
                    onError?.invoke(e)
                } else {
                    Thread.sleep(retryDelayMs)
                }
            }
        }
    }

    fun play() {
        mediaPlayer?.let {
            if (state == PlayerState.PAUSED || state == PlayerState.COMPLETED) {
                runCatching {
                    it.start()
                    state = PlayerState.PLAYING
                }.onFailure { state = PlayerState.ERROR }
            }
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                runCatching {
                    it.pause()
                    state = PlayerState.PAUSED
                }.onFailure { state = PlayerState.ERROR }
            }
        }
    }

    fun stop() {
        mediaPlayer?.let {
            runCatching {
                if (it.isPlaying) it.stop()
                it.reset()
                state = PlayerState.IDLE
            }.onFailure{ state = PlayerState.ERROR }
        }
        updateJob?.cancel()
        updateJob = null
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let {
            if (state == PlayerState.PLAYING || state == PlayerState.PAUSED) {
                runCatching { it.seekTo(positionMs) }.onFailure{ state = PlayerState.ERROR }
            }
        }
    }

    fun release() {
        stop()
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
        state = PlayerState.IDLE
        scope.coroutineContext.cancelChildren()
    }

    fun currentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun duration(): Int = mediaPlayer?.duration ?: 0
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
    fun getState(): PlayerState = state

    fun playbackPositionFlow(intervalMs: Long = 500L): Flow<Int> = callbackFlow {
        val player = mediaPlayer ?: return@callbackFlow
        updateJob = scope.launch {
            while (isActive && (state == PlayerState.PLAYING || state == PlayerState.PAUSED)) {
                trySend(player.currentPosition)
                delay(intervalMs)
            }
        }
        awaitClose { updateJob?.cancel() }
    }
}
