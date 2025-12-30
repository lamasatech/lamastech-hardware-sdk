package com.lamasatech.demo.sound

import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * MediaPlayerService is a modern foreground media playback service based on Media3 ExoPlayer and MediaSession.
 *
 * This service leverages Android's Media3 (ExoPlayer + MediaSession) to provide robust playback with:
 * - Automatic system-integrated notification with play/pause/seek/skip
 * - Lock screen controls, bluetooth/media button and Android Auto support
 * - Automatic management of playback focus and service lifecycle
 *
 * When a MediaController or compatible client connects, Media3 manages all playback controls.
 *
 * To use:
 * - Start this service, or bind to it and connect with MediaController (see MediaActivity).
 * - Set a MediaItem and control playback via MediaController or system UI.
 *
 * All notification handling and media control plumbing is handled for you by Media3.
 *
 * @see <a href="https://developer.android.com/media/media3">Media3 Docs</a>
 */
class MediaPlayerService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    /**
     * Initializes the ExoPlayer and attaches it to a MediaSession.
     * This makes playback discoverable to all system UIs and enables notification/lockscreen controls.
     */
    override fun onCreate() {
        super.onCreate()

        // Build ExoPlayer with recommended audio settings for media app
        player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true)
            .build()

        // Create a MediaSession that wires the player to system controls
        mediaSession = MediaSession.Builder(this, player)
            .build()
    }

    /**
     * Returns the app's main MediaSession for any connecting controller/client.
     * Media3 will manage the rest (including notification, media controls).
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    /**
     * Cleans up resources by releasing both the player and MediaSession on service destruction.
     */
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }
}