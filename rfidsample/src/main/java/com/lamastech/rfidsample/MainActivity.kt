package com.lamastech.rfidsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lamastech.rfidsample.databinding.ActivityMainBinding
import com.lamastech.rfidsample.rfid.RfidManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * RFID Sample for Zentron 8 (smdt_3288x / rk3288) devices.
 *
 * Collects RFID events from [RfidManager.rfidEvents]. The flow is active
 * while the Activity is STARTED and pauses automatically on STOP — no
 * manual bind/unbind required.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val scanLog = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rfidManager = RfidManager.instance

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.tvStatus.text = getString(R.string.status_active)
                rfidManager.rfidEvents.collect { uid -> onRfidScanned(uid) }
            }
            binding.tvStatus.text = getString(R.string.status_inactive)
        }

        binding.btnClear.setOnClickListener {
            scanLog.clear()
            binding.tvLastUid.text = getString(R.string.waiting)
            binding.tvLog.text = ""
        }
    }

    private fun onRfidScanned(uid: String) {
        binding.tvLastUid.text = uid
        scanLog.insert(0, "[${timeFormat.format(Date())}] $uid\n")
        if (scanLog.length > 2000) scanLog.setLength(2000)
        binding.tvLog.text = scanLog.toString()
    }
}
