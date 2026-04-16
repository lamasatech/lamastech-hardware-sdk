package com.lamasatech.samples

import android.os.Build
import android.os.Bundle
import com.lamasatech.kioskhardware.products.Model
import com.lamasatech.samples.databinding.ActivityDeviceInfoBinding

class DeviceInfoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityDeviceInfoBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "Device Info"

        val info = buildString {
            appendLine("Build.MODEL: ${Build.MODEL}")
            appendLine("Build.BOARD: ${Build.BOARD}")
            appendLine("Build.HARDWARE: ${Build.HARDWARE}")
            appendLine("Build.MANUFACTURER: ${Build.MANUFACTURER}")
            appendLine()
            try {
                appendLine("Model Type: ${Model.type::class.simpleName}")
                appendLine("Verify Hardware: ${Model.verifyHardware()}")
                appendLine()
                appendLine("LED Support: ${Model.isSupportLed}")
                appendLine("Temp Support: ${Model.isSupportTemp}")
                appendLine("Printer Support: ${Model.isSupportPrinter}")
                appendLine("QR Support: ${Model.isSupportQR}")
                appendLine("Facial Recognition: ${Model.isSupportFacialRecognition}")
                appendLine("15 Inch: ${Model.is15Inch}")
                appendLine("RFID Serial: ${Model.isSupportRfidSerial}")
                appendLine("RFID Keyboard: ${Model.isSupportRfidKeyboard}")
            } catch (e: Exception) {
                appendLine("Error: ${e.message}")
            }
        }
        b.tvInfo.text = info
    }
}
