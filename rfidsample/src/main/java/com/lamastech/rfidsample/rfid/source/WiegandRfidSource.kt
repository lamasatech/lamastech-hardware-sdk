package com.lamastech.rfidsample.rfid.source

import android.app.smdt.SmdtManager
import android.content.Context
import android.util.Log

/**
 * Reads RFID via the SMDT Wiegand hardware interface.
 * [SmdtManager.smdtReadWiegandData] returns "0" / null when no card is present.
 */
class WiegandRfidSource(private val context: Context) : RfidSource {

    private val tag = WiegandRfidSource::class.java.simpleName
    private var smdt: SmdtManager? = null

    override fun open() {
        smdt = SmdtManager.create(context)
    }

    override fun read(): String? {
        return try {
            smdt?.smdtReadWiegandData()
                ?.trim()
                ?.takeIf { it.isNotEmpty() && it != "0" }
        } catch (e: Exception) {
            Log.e(tag, "Wiegand read error", e)
            null
        }
    }

    override fun close() {
        smdt = null
    }
}
