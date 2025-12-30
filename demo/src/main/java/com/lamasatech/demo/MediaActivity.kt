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

class MediaActivity : AppCompatActivity() {

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var controller: MediaController? = null
    private val executor = ContextCompat.getMainExecutor(this)

    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_READY -> { /* ready */
                }

                Player.STATE_ENDED -> { /* finished */
                }

                Player.STATE_BUFFERING -> {}
                Player.STATE_IDLE -> {}
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            // Update play/pause button
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
    }

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

    override fun onStop() {
        controller?.release()
        controller = null
        super.onStop()
    }


    fun play() {
        val mediaItem = buildMediaItem()
        controller?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

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