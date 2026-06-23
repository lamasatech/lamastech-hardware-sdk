package com.lamastech.rfidsample.rfid.source

/**
 * A single RFID hardware source.
 *
 * [com.lamastech.rfidsample.rfid.RfidFlow] calls [open] once before polling starts, [read] on every tick,
 * and [close] when collection ends. [read] must be non-blocking — return null
 * immediately if no card data is available this tick.
 */
interface RfidSource {
    fun open() = Unit
    fun read(): String?
    fun close() = Unit
}