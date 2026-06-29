package com.lamastech.rfidsample.rfid.source

import com.lamastech.rfidsample.rfid.UidParser
import org.winplus.serial.utils.SerialPort
import java.io.File
import java.io.InputStream

/**
 * Reads RFID from a ZK ICM350A reader on [path] via UART.
 *
 * [open] opens the serial port and holds the [InputStream].
 * [read] checks [InputStream.available] — if bytes are waiting it reads them
 * immediately; otherwise it returns null. No background thread or channel
 * needed: the poll loop in RfidFlow already runs on Dispatchers.IO.
 */
class SerialRfidSource(
    private val path: String = "/dev/ttyS1",
    private val baudRate: Int = 9600,
) : RfidSource {

    private var serialPort: SerialPort? = null
    private var inputStream: InputStream? = null

    override fun open() {
        try {
            serialPort = SerialPort(File(path), baudRate, 0)
            inputStream = serialPort!!.inputStream
        } catch (e: Exception) {
            // Port stays null; read() will return null on every tick
        }
    }

    override fun read(): String? {
        val stream = inputStream ?: return null
        if (stream.available() == 0) return null
        val buffer = ByteArray(512)
        val size = stream.read(buffer)
        if (size <= 0) return null
        return UidParser.parse(buffer.copyOf(size))
    }

    override fun close() {
        serialPort?.close()
        serialPort = null
        inputStream = null
    }
}
