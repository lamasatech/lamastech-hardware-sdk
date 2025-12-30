package com.lamasatech.demo

import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.lamasatech.demo.sound.MediaPlayerService

/**
 * MediaActivity showcases a modern approach for controlling playback via Media3's MediaController.
 *
 * This activity demonstrates:
 * - How to asynchronously connect to a MediaSessionService (see [MediaPlayerService])
 * - How to play, pause, and control media through a system-integrated and background-aware player
 * - How to listen for player state changes (such as play/pause/completion), supporting UI updates
 *
 * Best practices:
 * - Only interact with [MediaController] after asynchronous build (see [controllerFuture])
 * - Always release the controller in [onStop] to avoid leaks
 * - Use player listeners to update your UI based on playback state or isPlaying changes
 *
 * The demo plays a test stream (see [buildMediaItem()]), but you can add browsing,
 * UI controls, playlist support and many more features with the same architecture.
 */
class MediaActivity : AppCompatActivity() {

    // Future used to asynchronously get the MediaController
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var controller: MediaController? = null
    private val executor = ContextCompat.getMainExecutor(this)

    // Listener receives host player events such as state changes for updating UI
    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_READY -> { /* Player is ready to start playback */ }
                Player.STATE_ENDED -> { /* Media playback finished */ }
                Player.STATE_BUFFERING -> { /* Buffering state */ }
                Player.STATE_IDLE -> { /* Nothing to play */ }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            // Update play/pause buttons or UI feedback here
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
    }

    /**
     * Connects asynchronously to the MediaPlayerService's MediaSession on activity start.
     * Registers a listener for playback state and isPlaying feedback.
     */
    override fun onStart() {
        super.onStart()

        controllerFuture = MediaController.Builder(
            this,
            SessionToken(
                this,
                ComponentName(this, MediaPlayerService::class.java)
            )
        ).buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(playerListener)
        }, executor)
    }

    /**
     * Cleans up and releases the MediaController before the activity is stopped.
     */
    override fun onStop() {
        controller?.release()
        controller = null
        super.onStop()
    }

    /**
     * Example method to play a media item using the connected controller.
     * Sets the media, prepares playback, and starts playing.
     */
    fun play() {
        val mediaItem = buildMediaItem()
        controller?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    /**
     * Builds a [MediaItem] with sample metadata. Replace with your own stream.
     */
    private fun buildMediaItem(): MediaItem {
        val mediaItem = MediaItem.Builder()
            .setUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Test Audio")
                    .setArtist("Media3")
                    .build()
            )
            .build()
        return mediaItem
    }
}