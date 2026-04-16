package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivityPowerBinding
import com.lamasatech.samples.util.safeCall

class PowerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityPowerBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "Power Management"

        b.btnReboot.setOnClickListener { safeCall(b.tvResult) { device?.reboot(this) } }
        b.btnShutdown.setOnClickListener { safeCall(b.tvResult) { device?.turnOff(this) } }
        b.btnScheduleReboot.setOnClickListener {
            val delay = b.etDelay.text.toString().toLongOrNull() ?: 60
            safeCall(b.tvResult) { device?.scheduleReboot(this, delay) }
        }
        b.btnSetAlarm.setOnClickListener {
            val off = b.etOffTime.text.toString().ifEmpty { "22:00" }
            val on = b.etOnTime.text.toString().ifEmpty { "08:00" }
            safeCall(b.tvResult) { device?.setTurnOffOnAlarm(off, on) }
        }
        b.btnAutoPower.setOnClickListener {
            safeCall(b.tvResult) {
                device?.setAutoPowerOnOff(true, intArrayOf(1, 1, 1, 1, 1, 0, 0), 8, 0, 22, 0)
            }
        }
    }
}
