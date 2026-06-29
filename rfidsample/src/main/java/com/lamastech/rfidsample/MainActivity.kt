package com.lamastech.rfidsample

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lamastech.rfidsample.databinding.ActivityMainBinding
import com.lamastech.rfidsample.rfid.RfidDefaults
import com.lamastech.rfidsample.rfid.RfidManager
import com.lamastech.rfidsample.rfid.SerialPortFinder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var rfidManager: RfidManager? = null
    private var collectJob: Job? = null

    private val baudRates = listOf(
        1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200,
        230400, 460800, 500000, 576000, 921600, 1000000,
        1152000, 1500000, 2000000, 2500000, 3000000, 3500000, 4000000,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ports = discoverSerialPorts()
        val recommended = RfidDefaults.detect()

        setupPortSpinner(ports, recommended)
        setupBaudSpinner(recommended)
        showHint(recommended)

        binding.btnStart.setOnClickListener { onStartClicked() }
        binding.btnClear.setOnClickListener {
            binding.tvLastUid.text = getString(R.string.waiting)
        }
    }

    private fun setupPortSpinner(ports: List<String>, recommended: RfidDefaults.PortConfig?) {
        if (ports.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_ports_found), Toast.LENGTH_SHORT).show()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ports)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPort.adapter = adapter

        if (recommended != null) {
            val idx = ports.indexOf(recommended.path)
            if (idx >= 0) binding.spinnerPort.setSelection(idx)
        }
    }

    private fun setupBaudSpinner(recommended: RfidDefaults.PortConfig?) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, baudRates)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBaud.adapter = adapter

        val targetBaud = recommended?.baudRate ?: 9600
        val idx = baudRates.indexOf(targetBaud)
        if (idx >= 0) binding.spinnerBaud.setSelection(idx)
    }

    private fun showHint(recommended: RfidDefaults.PortConfig?) {
        if (recommended == null) {
            binding.tvPortHint.visibility = View.GONE
            return
        }
        binding.tvPortHint.visibility = View.VISIBLE
        binding.tvPortHint.text =
            "Detected: ${recommended.modelLabel}\n" +
            "Recommended → ${recommended.path}  @  ${recommended.baudRate} baud"
    }

    private fun discoverSerialPorts(): List<String> = SerialPortFinder.find()

    private fun onStartClicked() {
        val port = binding.spinnerPort.selectedItem as? String
        val baudRate = binding.spinnerBaud.selectedItem as? Int

        if (port == null || baudRate == null) {
            Toast.makeText(this, getString(R.string.no_ports_found), Toast.LENGTH_SHORT).show()
            return
        }

        // Stop previous session
        collectJob?.cancel()
        rfidManager?.stop()

        binding.tvLastUid.text = getString(R.string.waiting)

        val manager = RfidManager(port, baudRate)
        rfidManager = manager

        collectJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                manager.rfidEvents.collect { uid ->
                    binding.tvLastUid.text = uid
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rfidManager?.stop()
    }
}
