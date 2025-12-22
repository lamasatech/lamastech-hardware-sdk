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

class MainActivity : AppCompatActivity(), FrameProcessor {

    private val audioInputFlow: AudioInputFlow = AudioInputFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // listen for camera
        bind.cameraPreview.setFrameProcessor(this)

        // mic stream
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            lifecycleScope.launch(Dispatchers.IO) {
                audioInputFlow.collect { data ->
                    // process mic data
                }
            }
        }

        // media player
        val player = MediaPlayerManager(this)
        player.prepare(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            onPrepared = { Log.i("MediaPlayerManager", "Prepared")},
            onError = { Log.e("MediaPlayerManager", "Error: ${it.message}") }
        )
        player.play()

    }

    override suspend fun process(frame: DualFrame) {
        // Process frame here
    }
}