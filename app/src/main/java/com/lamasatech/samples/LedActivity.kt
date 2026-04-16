package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivityLedBinding
import com.lamasatech.samples.util.safeCall

class LedActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityLedBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "LED Control"

        b.btnBlueOn.setOnClickListener { safeCall(b.tvResult) { device?.toggleBlueLight(1) } }
        b.btnBlueOff.setOnClickListener { safeCall(b.tvResult) { device?.toggleBlueLight(0) } }
        b.btnRedOn.setOnClickListener { safeCall(b.tvResult) { device?.toggleRedLight(1) } }
        b.btnRedOff.setOnClickListener { safeCall(b.tvResult) { device?.toggleRedLight(0) } }
        b.btnWhiteOn.setOnClickListener { safeCall(b.tvResult) { device?.toggleWhiteLight(1) } }
        b.btnWhiteOff.setOnClickListener { safeCall(b.tvResult) { device?.toggleWhiteLight(0) } }
        b.btnTurnOnGreen.setOnClickListener { safeCall(b.tvResult) { device?.turnOnGreenLight() } }
        b.btnTurnOnRed.setOnClickListener { safeCall(b.tvResult) { device?.turnOnRedLight() } }
        b.btnTurnOnWhite.setOnClickListener { safeCall(b.tvResult) { device?.turnOnWhiteLight() } }
        b.btnTurnOffAll.setOnClickListener { safeCall(b.tvResult) { device?.turnOffLight() } }
        b.btnIsSupportBlue.setOnClickListener { safeCall(b.tvResult) { device?.isSupportBlue() } }
        b.btnIsSupportRed.setOnClickListener { safeCall(b.tvResult) { device?.isSupportRed() } }
    }
}
