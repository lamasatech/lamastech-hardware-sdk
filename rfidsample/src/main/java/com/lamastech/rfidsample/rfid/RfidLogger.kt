package com.lamastech.rfidsample.rfid

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RfidLogger {

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    fun log(message: String) {
        _events.tryEmit(message)
    }
}
