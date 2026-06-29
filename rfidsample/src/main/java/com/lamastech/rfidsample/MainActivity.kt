package com.lamastech.rfidsample

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private enum class ReadState { IDLE, READING, STALE }

    private lateinit var binding: ActivityMainBinding
    private var rfidManager: RfidManager? = null
    private var collectJob: Job? = null
    private var readState = ReadState.IDLE

    // Guard: suppress dropdown listeners during initial adapter setup
    private var dropdownsReady = false

    // Port and baud of the currently active reading session
    private var activePort: String? = null
    private var activeBaud: String? = null

    private val baudRates = listOf(
        1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200,
        230400, 460800, 500000, 576000, 921600, 1000000,
        1152000, 1500000, 2000000, 2500000, 3000000, 3500000, 4000000,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ports = SerialPortFinder.find()
        val recommended = RfidDefaults.detect()

        setupPortDropdown(ports, recommended)
        setupBaudDropdown(recommended)
        showHint(recommended)
        applyReadState(ReadState.IDLE)

        dropdownsReady = true
        attachDropdownListeners()

        binding.btnStart.setOnClickListener { onStartClicked() }
        binding.btnClear.setOnClickListener {
            binding.tvLastUid.text = getString(R.string.waiting)
        }
    }

    // region Dropdown setup

    private fun setupPortDropdown(ports: List<String>, recommended: RfidDefaults.PortConfig?) {
        if (ports.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_ports_found), Toast.LENGTH_SHORT).show()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ports)
        binding.spinnerPort.setAdapter(adapter)
        val initial = recommended?.path?.takeIf { it in ports } ?: ports.firstOrNull() ?: return
        binding.spinnerPort.setText(initial, false)
    }

    private fun setupBaudDropdown(recommended: RfidDefaults.PortConfig?) {
        val labels = baudRates.map { it.toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, labels)
        binding.spinnerBaud.setAdapter(adapter)
        val target = (recommended?.baudRate ?: 9600).toString()
        binding.spinnerBaud.setText(target, false)
    }

    private fun attachDropdownListeners() {
        binding.spinnerPort.setOnItemClickListener { parent, _, pos, _ ->
            val picked = parent.getItemAtPosition(pos).toString()
            if (dropdownsReady && readState == ReadState.READING && picked != activePort)
                applyReadState(ReadState.STALE)
        }
        binding.spinnerBaud.setOnItemClickListener { parent, _, pos, _ ->
            val picked = parent.getItemAtPosition(pos).toString()
            if (dropdownsReady && readState == ReadState.READING && picked != activeBaud)
                applyReadState(ReadState.STALE)
        }
    }

    // endregion

    // region Hint banner

    private fun showHint(recommended: RfidDefaults.PortConfig?) {
        if (recommended == null) {
            binding.tvPortHint.visibility = View.GONE
            return
        }
        binding.tvPortHint.visibility = View.VISIBLE
        binding.tvPortHint.text = getString(
            R.string.hint_detected,
            recommended.modelLabel,
            recommended.path,
            recommended.baudRate,
        )
    }

    // endregion

    // region Start button state machine

    private fun applyReadState(state: ReadState) {
        readState = state
        val (labelRes, colorRes) = when (state) {
            ReadState.IDLE    -> R.string.btn_start   to R.color.btn_idle
            ReadState.READING -> R.string.btn_reading to R.color.btn_reading
            ReadState.STALE   -> R.string.btn_stale   to R.color.btn_stale
        }
        binding.btnStart.text = getString(labelRes)
        binding.btnStart.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))
    }

    // endregion

    // region RFID session

    private fun onStartClicked() {
        val port = binding.spinnerPort.text.toString().ifBlank { null }
        val baudRate = binding.spinnerBaud.text.toString().toIntOrNull()

        if (port == null || baudRate == null) {
            Toast.makeText(this, getString(R.string.no_ports_found), Toast.LENGTH_SHORT).show()
            return
        }

        collectJob?.cancel()
        rfidManager?.stop()

        activePort = port
        activeBaud = baudRate.toString()
        binding.tvLastUid.text = getString(R.string.waiting)
        applyReadState(ReadState.READING)

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

    // endregion

    override fun onDestroy() {
        super.onDestroy()
        rfidManager?.stop()
    }
}
