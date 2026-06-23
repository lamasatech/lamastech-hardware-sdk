package com.lamastech.rfidsample.rfid

import com.lamastech.rfidsample.rfid.source.SerialRfidSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

class RfidManager private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val rfidEvents: SharedFlow<String> = RfidFlow(
        sources = listOf(SerialRfidSource())
    ).flowOn(Dispatchers.IO)
        .shareIn(scope, SharingStarted.WhileSubscribed(), replay = 0)

    companion object {
        val instance: RfidManager by lazy { RfidManager() }
    }
}
