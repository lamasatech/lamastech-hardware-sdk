package com.lamasatech.demo.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.CoroutineContext

/**
 * Defines possible states for the [MediaPlayerManager] media session.
 */
enum class PlayerState {
    IDLE, PREPARING, PLAYING, PAUSED, COMPLETED, ERROR
}

/**
 * A robust utility to manage audio playback for streaming URLs via [MediaPlayer].
 *
 * Designed for easy integration with coroutines, providing:
 * - Stateful playback control (play, pause, stop, seek, release)
 * - Automatic retry on prepare failures (e.g., network flakiness)
 * - Support for event listeners (preparation success, error, completion)
 * - Coroutine cancellation safety for position monitoring flows
 *
 * Only one track is played at a time; each [prepare] releases the previous player.
 * Use [playbackPositionFlow] for periodic updates of the current track position.
 *
 * Example:
 * ```
 * val player = MediaPlayerManager(context)
 * player.prepare(url, autoplay = true, onPrepared = {...}, onError = {...})
 * player.pause()
 * player.seekTo(50_000)
 * ```
 *
 * @param context Android context, required for [MediaPlayer]
 * @param coroutineContext Coroutine context for background playback tasks (default IO)
 * @param prepareRetries How many times to retry preparing a source before giving up
 * @param retryDelayMs Milliseconds between retries if a prepare error occurs
 */
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

    /**
     * Prepares a new [MediaPlayer] asynchronously with a URL source.
     * Safe for repeated usage: previous instance is released.
     * Invokes [onPrepared] when ready, starts playback if [autoplay] is true, and
     * calls [onError] with the last error after exhausting [prepareRetries].
     *
     * @param url The audio stream location.
     * @param autoplay Start playing automatically when ready.
     * @param onPrepared Callback upon successful preparation.
     * @param onError Callback on fatal error or after all retries fail.
     */
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

    /**
     * Starts audio playback if properly prepared or paused.
     */
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

    /**
     * Pauses the currently playing audio if applicable.
     */
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

    /**
     * Stops and resets the playback session, cancels position updates.
     */
    fun stop() {
        mediaPlayer?.let {
            runCatching {
                if (it.isPlaying) it.stop()
                it.reset()
                state = PlayerState.IDLE
            }.onFailure { state = PlayerState.ERROR }
        }
        updateJob?.cancel()
        updateJob = null
    }

    /**
     * Seeks playback to the given position in milliseconds.
     * No-op if player is not prepared or state is not valid for seeking.
     *
     * @param positionMs Millisecond offset for playback position
     */
    fun seekTo(positionMs: Int) {
        mediaPlayer?.let {
            if (state == PlayerState.PLAYING || state == PlayerState.PAUSED) {
                runCatching { it.seekTo(positionMs) }.onFailure { state = PlayerState.ERROR }
            }
        }
    }

    /**
     * Releases all resources and resets playback state.
     */
    fun release() {
        stop()
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
        state = PlayerState.IDLE
        scope.coroutineContext.cancelChildren()
    }

    /**
     * Returns the current playback position in milliseconds.
     */
    fun currentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    /**
     * Returns the audio track duration in milliseconds.
     */
    fun duration(): Int = mediaPlayer?.duration ?: 0

    /**
     * Returns true if a track is currently playing.
     */
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    /**
     * Returns the internal playback state.
     */
    fun getState(): PlayerState = state

    /**
     * Emits the current playback position periodically (useful for UI).
     * The flow emits while in PLAYING or PAUSED state and is automatically closed otherwise.
     *
     * @param intervalMs Time between position updates in ms.
     * @return A cold flow of playback positions (ms).
     */
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