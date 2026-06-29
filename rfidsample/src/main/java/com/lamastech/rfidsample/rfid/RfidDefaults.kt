package com.lamastech.rfidsample.rfid

import android.os.Build

/**
 * Recommended RFID serial port and baud rate per device model.
 *
 * Derived from ModelType + SerialPath in the kioskhardware library:
 *   Zentron (rk3288 / smdt_3288x)  → ttyS1 @ 9600
 *   OctopusA83 / DefaultSMDT       → ttyS3 @ 115200
 *   Visipoint 15                    → ttyS4 @ 115200
 *   RK3568 (Zentron_5)              → ttyS3 @ 115200
 *   RK3576 (LT-ACCRK3576-poe)      → ttyS3 @ 115200
 */
object RfidDefaults {

    data class PortConfig(
        val path: String,
        val baudRate: Int,
        val modelLabel: String,
    )

    private val exactMap: Map<String, PortConfig> = mapOf(
        "LT-Zentron8"      to PortConfig("/dev/ttyS1", 9600,   "LT-Zentron8"),
        "LT-Zentron15"     to PortConfig("/dev/ttyS1", 9600,   "LT-Zentron15"),
        "LD-AITemp"        to PortConfig("/dev/ttyS1", 9600,   "LD-AITemp"),
        "rk3288"           to PortConfig("/dev/ttyS1", 9600,   "rk3288"),
        "rk3288_tdx"       to PortConfig("/dev/ttyS1", 9600,   "rk3288_tdx"),
        "Visipoint 15"     to PortConfig("/dev/ttyS4", 115200, "Visipoint 15"),
        "Octopus A83 F1"   to PortConfig("/dev/ttyS3", 115200, "Octopus A83 F1"),
        "Zentron_5"        to PortConfig("/dev/ttyS3", 115200, "Zentron 5 (RK3568)"),
        "LT-ACCRK3576-poe" to PortConfig("/dev/ttyS3", 115200, "LT-ACCRK3576 (RK3576)"),
    )

    /** Returns the recommended config for the running device, or null if unrecognised. */
    fun detect(): PortConfig? {
        val model = Build.MODEL
        exactMap[model]?.let { return it }

        return when {
            model.startsWith("rk3288", ignoreCase = true) ||
            model.contains("zentron", ignoreCase = true) ->
                PortConfig("/dev/ttyS1", 9600, "$model (Zentron/rk3288)")

            model.contains("RK3576", ignoreCase = true) ->
                PortConfig("/dev/ttyS3", 115200, "$model (RK3576)")

            model.contains("3568", ignoreCase = true) ->
                PortConfig("/dev/ttyS3", 115200, "$model (RK3568/S3568)")

            model.contains("SMDT", ignoreCase = true) ->
                PortConfig("/dev/ttyS3", 115200, "$model (DefaultSMDT)")

            else -> null
        }
    }
}
