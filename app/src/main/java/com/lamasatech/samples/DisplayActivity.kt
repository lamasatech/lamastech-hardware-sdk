package com.lamasatech.samples

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import com.lamasatech.samples.databinding.ActivityDisplayBinding
import com.lamasatech.samples.util.safeCall

class DisplayActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityDisplayBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "Display"

        // Helper to wire SeekBar to its value label
        fun wireSeekBar(seekBar: SeekBar, label: TextView) {
            label.text = "Value: ${seekBar.progress}"
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                    label.text = "Value: $progress"
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }

        // 1. setBrightness
        wireSeekBar(b.seekBrightness, b.tvBrightnessValue)
        b.btnSetBrightness.setOnClickListener {
            val v = b.seekBrightness.progress
            safeCall(b.tvResult) { device?.setBrightness(this, v); "setBrightness($v)" }
        }

        // 2. setLcdBrightness
        wireSeekBar(b.seekLcdBrightness, b.tvLcdBrightnessValue)
        b.btnSetLcdBrightness.setOnClickListener {
            val screenId = b.etScreenId.text.toString().toIntOrNull() ?: 0
            val v = b.seekLcdBrightness.progress
            safeCall(b.tvResult) { device?.setLcdBrightness(screenId, v) }
        }

        // 3. setLcdBackLight
        wireSeekBar(b.seekLcdBackLight, b.tvLcdBackLightValue)
        b.btnSetLcdBackLight.setOnClickListener {
            val v = b.seekLcdBackLight.progress
            safeCall(b.tvResult) { device?.setLcdBackLight(v) }
        }

        // 4. setEDPBackLight
        wireSeekBar(b.seekEdpBackLight, b.tvEdpBackLightValue)
        b.btnSetEdpBackLight.setOnClickListener {
            val v = b.seekEdpBackLight.progress
            safeCall(b.tvResult) { device?.setEDPBackLight(v) }
        }

        // 5. setLcdBackLightEnable
        b.btnBacklightOn.setOnClickListener {
            val screenId = b.etScreenId.text.toString().toIntOrNull() ?: 0
            safeCall(b.tvResult) { device?.setLcdBackLightEnable(screenId, true) }
        }
        b.btnBacklightOff.setOnClickListener {
            val screenId = b.etScreenId.text.toString().toIntOrNull() ?: 0
            safeCall(b.tvResult) { device?.setLcdBackLightEnable(screenId, false) }
        }
        b.btnGetBacklight.setOnClickListener {
            val screenId = b.etScreenId.text.toString().toIntOrNull() ?: 0
            safeCall(b.tvResult) { device?.getLcdBackLightEnable(screenId) }
        }

        // Rotation
        b.btnSetRotation.setOnClickListener {
            val degree = b.etRotation.text.toString().ifEmpty { "0" }
            safeCall(b.tvResult) { device?.setRotation(degree) }
        }
        b.btnGetRotation.setOnClickListener {
            val screenId = b.etScreenId.text.toString().toIntOrNull() ?: 0
            safeCall(b.tvResult) { device?.getDisplayRotation(screenId) }
        }

        // Timeout
        b.btnSetTimeout.setOnClickListener {
            val timeout = b.etTimeout.text.toString().toIntOrNull() ?: 60000
            safeCall(b.tvResult) {
                device?.setTimeOut(timeout) { result ->
                    runOnUiThread { b.tvResult.text = "Result: $result" }
                }
            }
        }
    }
}
