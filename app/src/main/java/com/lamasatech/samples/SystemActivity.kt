package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivitySystemBinding
import com.lamasatech.samples.util.safeCall

class SystemActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySystemBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "System"

        b.btnUpdateFirmware.setOnClickListener {
            safeCall(b.tvResult) { device?.updateFirmware(b.etFirmwarePath.text.toString()) }
        }
        b.btnSetTimezone.setOnClickListener {
            safeCall(b.tvResult) { device?.setTimeZone(b.etTimezone.text.toString().ifEmpty { "Asia/Riyadh" }) }
        }
        b.btnGetNtp.setOnClickListener { safeCall(b.tvResult) { device?.getNtpServer() } }
        b.btnSetNtp.setOnClickListener {
            safeCall(b.tvResult) { device?.setNtpServer(b.etNtpUrl.text.toString().ifEmpty { "pool.ntp.org" }) }
        }
        b.btnSetVolume.setOnClickListener {
            safeCall(b.tvResult) { device?.setVolume(this, b.seekVolume.progress) }
        }
    }
}
