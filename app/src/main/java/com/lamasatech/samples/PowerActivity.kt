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
        b.btnAutoPowerCustom.setOnClickListener {
            val week = parseWeek(b.etAutoWeek.text.toString())
            val (onHour, onMinute) = parseTime(b.etAutoOnTime.text.toString(), 8, 0)
            val (offHour, offMinute) = parseTime(b.etAutoOffTime.text.toString(), 22, 0)
            val enable = b.cbAutoPowerEnable.isChecked
            safeCall(b.tvResult) {
                device?.setAutoPowerOnOff(enable, week, onHour, onMinute, offHour, offMinute)
            }
        }
        b.btnAutoPowerDisable.setOnClickListener {
            safeCall(b.tvResult) { device?.setAutoPowerOnOff(false, null, 0, 0, 0, 0) }
        }
        b.btnGetAutoPowerSchedule.setOnClickListener {
            safeCall(b.tvResult) {
                val schedule = device?.getAutoPowerSchedule() ?: return@safeCall "no device"
                "enabled=${schedule.enabled} nextPowerOn=${schedule.nextPowerOn} nextPowerOff=${schedule.nextPowerOff}"
            }
        }
    }

    /** `"1,0,1,0,1,0,1"` (Sun-Sat) -> IntArray; blank keeps every day enabled. */
    private fun parseWeek(raw: String): IntArray? {
        if (raw.isBlank()) return intArrayOf(1, 1, 1, 1, 1, 1, 1)
        val parts = raw.split(',').mapNotNull { it.trim().toIntOrNull() }
        return if (parts.size == 7) parts.toIntArray() else intArrayOf(1, 1, 1, 1, 1, 1, 1)
    }

    /** `"HH:mm"` -> (hour, minute); falls back to [defHour]:[defMinute] if blank/unparseable. */
    private fun parseTime(raw: String, defHour: Int, defMinute: Int): Pair<Int, Int> {
        val parts = raw.split(':').mapNotNull { it.trim().toIntOrNull() }
        return if (parts.size == 2) parts[0] to parts[1] else defHour to defMinute
    }
}
