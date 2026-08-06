package com.lamasatech.samples

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.Toast
import com.lamasatech.kioskhardware.scanner.ScanCallback
import com.lamasatech.kioskhardware.scanner.SerialManager
import com.lamasatech.kioskhardware.scanner.SerialPortConfig
import com.lamasatech.kioskhardware.scanner.SerialPortFinder
import com.lamasatech.samples.databinding.ActivityScannerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Demonstrates the three ways [SerialManager] exposes RFID/QR scans:
 * a direct one-shot read, a continuous [kotlinx.coroutines.flow.Flow], and an
 * old-school callback — plus how to override the auto-detected port/baud
 * with a value picked from the device's actual serial ports.
 *
 * [serialManager] is constructed once and reused for the life of this
 * Activity: port/baud is a per-call parameter now (see [SerialManager]'s own
 * doc), so picking a different port from the dropdowns doesn't require a new
 * instance — only currently-running flows/callbacks (started with whatever
 * override was selected at the time) need to be restarted to pick it up,
 * which "Apply" below does.
 *
 * [activityScope] only drives this Activity's own coroutines (collecting
 * flows into the UI); it's never handed to [SerialManager], which always
 * owns its own internal scope and is torn down explicitly via
 * [SerialManager.stop] in [onDestroy].
 */
class ScannerActivity : BaseActivity() {

    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val baudRates = listOf(
        1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200,
        230400, 460800, 500000, 576000, 921600, 1000000,
        1152000, 1500000, 2000000, 2500000, 3000000, 3500000, 4000000,
    )

    private lateinit var binding: ActivityScannerBinding
    private lateinit var serialManager: SerialManager

    private var rfidFlowJob: Job? = null
    private var qrFlowJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "RFID / QR Sample"

        serialManager = SerialManager(context = this)
        setupPortDropdowns()

        binding.btnReadRfid.setOnClickListener { readRfidOnce() }
        binding.btnReadQr.setOnClickListener { readQrOnce() }
        binding.btnToggleRfidFlow.setOnClickListener { toggleRfidFlow() }
        binding.btnToggleQrFlow.setOnClickListener { toggleQrFlow() }
        binding.btnToggleRfidCallback.setOnClickListener { toggleRfidCallback() }
        binding.btnToggleQrCallback.setOnClickListener { toggleQrCallback() }
        binding.btnApplyRfidPort.setOnClickListener { applyNewPortSelection() }
        binding.btnApplyQrPort.setOnClickListener { applyNewPortSelection() }
    }

    // region Port/baud dropdowns

    /** Populates both port/baud dropdown pairs and pre-fills them with this model's recommended port. */
    private fun setupPortDropdowns() {
        val ports = SerialPortFinder.find()
        if (ports.isEmpty()) {
            Toast.makeText(this, "No serial ports found on this device", Toast.LENGTH_SHORT).show()
        }
        val portAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ports)
        val baudAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, baudRates.map { it.toString() })

        binding.spinnerRfidPort.setAdapter(portAdapter)
        binding.spinnerRfidBaud.setAdapter(baudAdapter)
        binding.spinnerQrPort.setAdapter(portAdapter)
        binding.spinnerQrBaud.setAdapter(baudAdapter)

        val recommendedRfid = SerialManager.recommendedRfidPort()
        binding.spinnerRfidPort.setText(recommendedRfid?.path?.takeIf { it in ports } ?: ports.firstOrNull().orEmpty(), false)
        binding.spinnerRfidBaud.setText((recommendedRfid?.baudRate ?: 9600).toString(), false)
        binding.tvRfidPortHint.text = recommendedRfid
            ?.let { "Recommended for this model: ${it.path} @ ${it.baudRate} baud" }
            ?: "This model declares no RFID serial port — pick one manually"

        // recommendedQrPort() is suspend — its last-resort tier can probe real
        // hardware — so the QR dropdown is pre-filled asynchronously.
        activityScope.launch {
            val recommendedQr = SerialManager.recommendedQrPort()
            binding.spinnerQrPort.setText(recommendedQr?.path?.takeIf { it in ports } ?: ports.firstOrNull().orEmpty(), false)
            binding.spinnerQrBaud.setText((recommendedQr?.baudRate ?: 9600).toString(), false)
            binding.tvQrPortHint.text = recommendedQr
                ?.let { "Recommended for this model: ${it.path} @ ${it.baudRate} baud" }
                ?: "This model declares no QR serial port — pick one manually"
        }
    }

    private fun selectedRfidPort(): SerialPortConfig? {
        val path = binding.spinnerRfidPort.text.toString().ifBlank { return null }
        val baud = binding.spinnerRfidBaud.text.toString().toIntOrNull() ?: return null
        return SerialPortConfig(path, baud)
    }

    private fun selectedQrPort(): SerialPortConfig? {
        val path = binding.spinnerQrPort.text.toString().ifBlank { return null }
        val baud = binding.spinnerQrBaud.text.toString().toIntOrNull() ?: return null
        return SerialPortConfig(path, baud)
    }

    // endregion

    // region SerialManager lifecycle

    /**
     * Stops any active flow/callback so they stop observing whichever port
     * they were started with. [serialManager] itself isn't rebuilt — port is
     * a per-call parameter now, so the *next* read/flow/callback just needs
     * to pass the freshly-selected [selectedRfidPort]/[selectedQrPort]
     * override, which every call site below already does.
     */
    private fun applyNewPortSelection() {
        rfidFlowJob?.cancel()
        rfidFlowJob = null
        qrFlowJob?.cancel()
        qrFlowJob = null
        serialManager.setRfidCallback(callback = null)
        serialManager.setQrCallback(callback = null)

        binding.btnToggleRfidFlow.text = "Start flow"
        binding.btnToggleQrFlow.text = "Start flow"
        binding.btnToggleRfidCallback.text = "Enable callback"
        binding.btnToggleQrCallback.text = "Enable callback"

        val rfid = selectedRfidPort()
        val qr = selectedQrPort()
        appendLog("Port selection applied — rfid=${rfid?.path}@${rfid?.baudRate}, qr=${qr?.path}@${qr?.baudRate}")
    }

    // endregion

    /** Direct read: suspends until one RFID scan arrives, or times out. */
    private fun readRfidOnce() {
        binding.tvRfidResult.text = "Reading…"
        activityScope.launch {
            val uid = serialManager.readRfid(override = selectedRfidPort())
            binding.tvRfidResult.text = uid ?: "No RFID scan received"
            appendLog("readRfid() -> ${uid ?: "timeout"}")
        }
    }

    /** Direct read: suspends until one QR/barcode scan arrives, or times out. */
    private fun readQrOnce() {
        binding.tvQrResult.text = "Reading…"
        activityScope.launch {
            val code = serialManager.readQr(override = selectedQrPort())
            binding.tvQrResult.text = code ?: "No QR scan received"
            appendLog("readQr() -> ${code ?: "timeout"}")
        }
    }

    /** Flow: continuous stream of RFID UIDs, toggled on/off by the same button. */
    private fun toggleRfidFlow() {
        binding.tvRfidResult.text = "—"
        val running = rfidFlowJob
        if (running != null) {
            running.cancel()
            rfidFlowJob = null
            binding.btnToggleRfidFlow.text = "Start flow"
            appendLog("rfidFlow() stopped")
            return
        }
        binding.btnToggleRfidFlow.text = "Stop flow"
        appendLog("rfidFlow() started")
        rfidFlowJob = activityScope.launch {
            serialManager.rfidFlow(override = selectedRfidPort()).collect { uid ->
                binding.tvRfidResult.text = uid
                appendLog("rfidFlow() -> $uid")
            }
        }
    }

    /** Flow: continuous stream of QR/barcode payloads, toggled on/off by the same button. */
    private fun toggleQrFlow() {
        binding.tvQrResult.text = "—"
        val running = qrFlowJob
        if (running != null) {
            running.cancel()
            qrFlowJob = null
            binding.btnToggleQrFlow.text = "Start flow"
            appendLog("qrFlow() stopped")
            return
        }
        binding.btnToggleQrFlow.text = "Stop flow"
        appendLog("qrFlow() started")
        qrFlowJob = activityScope.launch {
            serialManager.qrFlow(override = selectedQrPort()).collect { code ->
                binding.tvQrResult.text = code
                appendLog("qrFlow() -> $code")
            }
        }
    }

    /** Callback: assign via [SerialManager.setRfidCallback] to observe, pass `null` to stop and release it. */
    private fun toggleRfidCallback() {
        binding.tvRfidResult.text = "—"
        if (serialManager.rfidCallback != null) {
            serialManager.setRfidCallback(callback = null)
            binding.btnToggleRfidCallback.text = "Enable callback"
            appendLog("rfidCallback cleared")
            return
        }
        binding.btnToggleRfidCallback.text = "Disable callback"
        appendLog("rfidCallback set")
        serialManager.setRfidCallback(override = selectedRfidPort()) { uid ->
            binding.tvRfidResult.text = uid
            appendLog("rfidCallback -> $uid")
        }
    }

    /** Callback: same set-to-null-to-clear behavior as [toggleRfidCallback], for QR. */
    private fun toggleQrCallback() {
        binding.tvQrResult.text = "—"
        if (serialManager.qrCallback != null) {
            serialManager.setQrCallback(callback = null)
            binding.btnToggleQrCallback.text = "Enable callback"
            appendLog("qrCallback cleared")
            return
        }
        binding.btnToggleQrCallback.text = "Disable callback"
        appendLog("qrCallback set")
        serialManager.setQrCallback(override = selectedQrPort()) { code ->
            binding.tvQrResult.text = code
            appendLog("qrCallback -> $code")
        }
    }

    private fun appendLog(message: String) {
        val timestamp = DateFormat.format("HH:mm:ss", System.currentTimeMillis())
        binding.tvLog.append("\n[$timestamp] $message")
    }

    override fun onDestroy() {
        serialManager.stop()
        activityScope.cancel()
        super.onDestroy()
    }
}
