package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivityHardwareBinding
import com.lamasatech.samples.util.safeCall

class HardwareActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityHardwareBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "Hardware"

        b.btnUsbOn.setOnClickListener { safeCall(b.tvResult) { device?.setUsbPower(1) } }
        b.btnUsbOff.setOnClickListener { safeCall(b.tvResult) { device?.setUsbPower(0) } }
        b.btnGetSdPath.setOnClickListener { safeCall(b.tvResult) { device?.getSDCardPath(this) } }
        b.btnFanOn.setOnClickListener { safeCall(b.tvResult) { device?.setFanOnOff(1) } }
        b.btnFanOff.setOnClickListener { safeCall(b.tvResult) { device?.setFanOnOff(0) } }
        b.btnScreenshot.setOnClickListener { safeCall(b.tvResult) { device?.getScreenShot("/sdcard/test_screenshot.png") } }
        b.btnScreenshotBitmap.setOnClickListener {
            safeCall(b.tvResult) {
                val bmp = device?.getScreenShotBitmap()
                if (bmp != null) "Bitmap: ${bmp.width}x${bmp.height}" else "null"
            }
        }
    }
}
