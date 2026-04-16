package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.kioskhardware.products.model.ModeOptions
import com.lamasatech.samples.databinding.ActivityGpioRelayBinding
import com.lamasatech.samples.util.safeCall

class GpioRelayActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityGpioRelayBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "GPIO & Relay"

        b.btnGetIoPort.setOnClickListener {
            val port = b.etPort.text.toString().toIntOrNull() ?: 1
            safeCall(b.tvResult) { device?.getIOPortStatus(port) }
        }
        b.btnGetGpioDir.setOnClickListener {
            val port = b.etPort.text.toString().toIntOrNull() ?: 1
            safeCall(b.tvResult) { device?.getGpioDirection(port) }
        }
        b.btnSetGpioDir.setOnClickListener {
            val port = b.etGpioPort.text.toString().toIntOrNull() ?: 1
            val dir = b.etGpioDir.text.toString().toIntOrNull() ?: 1
            val value = b.etGpioValue.text.toString().toIntOrNull() ?: 0
            safeCall(b.tvResult) { device?.setGpioDirection(port, dir, value) }
        }
        b.btnSetRelayMode.setOnClickListener {
            val mode = b.etRelayMode.text.toString().toIntOrNull() ?: 1
            val delay = b.etRelayDelay.text.toString().toIntOrNull() ?: 3
            safeCall(b.tvResult) { device?.setRelayIoMode(mode, delay) }
        }
        b.btnSetRelayValue.setOnClickListener {
            val value = b.etRelayValue.text.toString().toIntOrNull() ?: 0
            safeCall(b.tvResult) { device?.setRelayIoValue(value) }
        }
        b.btnGetRelayMode.setOnClickListener { safeCall(b.tvResult) { device?.getRelayIoMode() } }
        b.btnGetRelayValue.setOnClickListener { safeCall(b.tvResult) { device?.getRelayIoValue() } }
        b.btnOpenRelay.setOnClickListener { safeCall(b.tvResult) { device?.applyOpenRelay(ModeOptions.MODE1, 3) } }
        b.btnCloseRelay.setOnClickListener { safeCall(b.tvResult) { device?.applyCloseRelay(ModeOptions.MODE1) } }
    }
}
