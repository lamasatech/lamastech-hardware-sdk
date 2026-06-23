package com.lamastech.rfidsample.rfid

import com.lamastech.rfidsample.rfid.source.RfidSource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive

/**
 * A [Flow]<[String]> that polls a list of [com.lamastech.rfidsample.rfid.source.RfidSource]s in a single loop.
 *
 * On each tick, sources are checked in order and the first non-null result is
 * emitted. Because both sources are read within the same tick, the same
 * physical card cannot produce more than one emission — no deduplication needed.
 *
 * Lifecycle:
 *  - [collect] called  → opens all sources, starts the poll loop
 *  - collector cancels → closes all sources, loop exits
 */
class RfidFlow(
    private val sources: List<RfidSource>,
    private val pollIntervalMs: Long = POLL_INTERVAL_MS,
) : Flow<String> {

    override suspend fun collect(collector: FlowCollector<String>) {
        sources.forEach { it.open() }
        try {
            while (currentCoroutineContext().isActive) {
                val uid = runCatching { sources.firstNotNullOfOrNull { it.read() } }.getOrNull()
                if (uid != null) collector.emit(uid)
                delay(pollIntervalMs)
            }
        } finally {
            sources.forEach { it.close() }
        }
    }

    companion object {
        private const val POLL_INTERVAL_MS = 100L
    }
}
