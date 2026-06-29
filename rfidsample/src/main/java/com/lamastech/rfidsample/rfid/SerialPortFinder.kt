package com.lamastech.rfidsample.rfid

import java.io.File

object SerialPortFinder {

    /**
     * Returns all serial port device paths available on this device.
     *
     * Primary: parses /proc/tty/drivers (kernel's driver registry) to get
     * the device path prefix for every entry whose type is "serial", then
     * cross-references with actual files in /dev/.
     *
     * Fallback: /sys/class/tty — only entries that have a "device" child
     * are hardware-backed; virtual pseudo-terminals (pts, ptmx) don't.
     */
    fun find(): List<String> {
        val fromDrivers = findViaDrivers()
        if (fromDrivers.isNotEmpty()) return fromDrivers
        return findViaSysfs()
    }

    private fun findViaDrivers(): List<String> {
        val prefixes = runCatching {
            File("/proc/tty/drivers").readLines()
                .map { it.trim().split(Regex("\\s+")) }
                .filter { parts -> parts.size >= 5 && parts.last() == "serial" }
                .map { parts -> parts[1] }
        }.getOrElse { return emptyList() }

        if (prefixes.isEmpty()) return emptyList()

        return File("/dev").listFiles()
            ?.filter { file -> prefixes.any { file.absolutePath.startsWith(it) } }
            ?.map { it.absolutePath }
            ?.sorted()
            ?: emptyList()
    }

    private fun findViaSysfs(): List<String> =
        File("/sys/class/tty").listFiles()
            ?.filter { File(it, "device").exists() }
            ?.mapNotNull { tty -> File("/dev", tty.name).takeIf { it.exists() }?.absolutePath }
            ?.sorted()
            ?: emptyList()
}
