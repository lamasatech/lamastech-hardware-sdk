package com.lamastech.rfidsample.rfid

import android.content.Context
import com.lamastech.rfidsample.rfid.source.SerialRfidSource
import com.lamastech.rfidsample.rfid.source.WiegandRfidSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

class RfidManager private constructor(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val rfidEvents: SharedFlow<String> = RfidFlow(
        sources = listOf(
            WiegandRfidSource(context),
            SerialRfidSource(),
        )
    ).flowOn(Dispatchers.IO)
        .shareIn(scope, SharingStarted.WhileSubscribed(), replay = 0)

    companion object {
        @Volatile
        private var instance: RfidManager? = null

        fun getInstance(context: Context): RfidManager =
            instance ?: synchronized(this) {
                instance ?: RfidManager(context.applicationContext).also { instance = it }
            }
    }
}
