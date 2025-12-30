package com.lamasatech.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.lamasatech.demo.camera.DualFrame
import com.lamasatech.demo.camera.FrameProcessor
import com.lamasatech.demo.databinding.ActivityMainBinding
import com.lamasatech.demo.mic.AudioInputFlow
import com.lamasatech.demo.sound.MediaPlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * MainActivity is the demo application's main screen and orchestrates the core hardware SDK components.
 *
 * This activity demonstrates:
 *  - Camera preview and frame processing, through the UI and [FrameProcessor] interface.
 *  - Real-time microphone input capture, delivered via a [AudioInputFlow] coroutine Flow.
 *  - Streaming and playback of remote audio via [MediaPlayerManager].
 *
 * On launch, the activity:
 *  1. Sets up view binding for the UI.
 *  2. Starts listening for camera frames (assigns this instance as the [FrameProcessor]).
 *  3. Requests microphone permission; if granted, launches a coroutine on IO to collect and process
 *     real-time audio streams.
 *  4. Initializes and prepares a media stream for playback, automatically calling play on successful preparation.
 *
 * Customize [process] for your own image processing pipeline—face detection, frame analysis etc.
 *
 * @see FrameProcessor for camera processing callbacks.
 * @see AudioInputFlow for microphone stream.
 * @see MediaPlayerManager for audio playback.
 */
class MainActivity : AppCompatActivity(), FrameProcessor {

    /**
     * Provides a coroutine-based Flow of microphone PCM data.
     */
    private val audioInputFlow: AudioInputFlow = AudioInputFlow()

    /**
     * App entry point: sets up all hardware and streaming components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Connect UI's camera preview to this FrameProcessor for per-frame processing.
        bind.cameraPreview.setFrameProcessor(this)

        // Begin collecting microphone audio if permission granted.
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            lifecycleScope.launch(Dispatchers.IO) {
                audioInputFlow.collect { data ->
                    // Process mic data (write to file, do speech, etc)
                }
            }
        }

        // Prepare and play a remote audio stream with a managed media player.
        val player = MediaPlayerManager(this)
        player.prepare(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            onPrepared = { Log.i("MediaPlayerManager", "Prepared")},
            onError = { Log.e("MediaPlayerManager", "Error: ${it.message}") }
        )
        player.play()
    }

    /**
     * Receives every camera frame pair (RGB + IR) for image processing.
     * Override this to implement frame-by-frame analytics, e.g. face tracking or liveness tests.
     *
     * @param frame Contains a synchronized RGB and IR image plus frame count.
     */
    override suspend fun process(frame: DualFrame) {
        // Implement image/frame-processing logic for your SDK integration here
    }
}