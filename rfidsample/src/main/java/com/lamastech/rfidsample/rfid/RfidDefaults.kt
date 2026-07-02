package com.lamastech.rfidsample.rfid

import android.os.Build
import kotlin.reflect.typeOf

/**
 * Recommended RFID serial port and baud rate per device model.
 */
object RfidDefaults {

    data class PortConfig(
        val path: String,
        val baudRate: Int,
        val modelLabel: String,
    )

    /** Returns the recommended config for the running device, or null if unrecognised. */
    fun detect(): PortConfig {
        val model = Build.MODEL
        return ModelType.detect().let {
            PortConfig(it.serial.path, it.serial.bauteRate, model)
        }
    }

}
