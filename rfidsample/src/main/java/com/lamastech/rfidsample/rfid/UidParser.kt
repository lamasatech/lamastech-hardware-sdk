package com.lamastech.rfidsample.rfid

import java.math.BigInteger
import java.util.Locale

/**
 * Pure function that converts a raw byte array from the ZK RFID ICM350A
 * serial reader into a decimal UID string.
 *
 * Separated from SerialRfidSource so it can be unit-tested without hardware.
 *
 * Protocol layout (Zentron 8 / rk3288):
 *   byte[5] = payload length + 1 (includes status byte)
 *   byte[7..7+len] = UID bytes (little-endian)
 *
 * For 8-byte UIDs the middle 4 bytes are extracted to match the 4-byte UID
 * format used across all Lamasa kiosk models.
 */
object UidParser {

    fun parse(bytes: ByteArray): String? {
        if (bytes.size <= 6) return null

        val uidLength = (bytes[5].toInt() and 0xFF) - 1
        if (uidLength <= 0 || 7 + uidLength > bytes.size) return null

        val uidBytes = ByteArray(uidLength) { bytes[7 + it] }
        reverse(uidBytes)

        var hex = byteArrToHex(uidBytes)
        if (hex.length >= 16) {
            hex = hex.substring(6, hex.length - 2)
        }

        return try {
            hexToBigInteger(hex).toString()
                .trimStart('0')
                .ifEmpty { "0" }
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun byte2Hex(inByte: Byte?): String {
        return String.format("%02x", inByte).uppercase(Locale.getDefault())
    }

    fun byteArrToHex(inBytArr: ByteArray): String {
        val strBuilder = StringBuilder()
        for (b in inBytArr) {
            strBuilder.append(byte2Hex(b))
        }
        return strBuilder.toString()
    }

    fun reverse(array: ByteArray?) {
        if (array == null) return
        for (i in 0..<array.size / 2) {
            val temp = array[i]
            array[i] = array[array.size - i - 1]
            array[array.size - i - 1] = temp
        }
    }

    fun hexToBigInteger(hex: String): BigInteger {
        return BigInteger(hex, 16)
    }
}
