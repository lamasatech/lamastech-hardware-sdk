package com.lamasatech.samples.util

import android.widget.TextView
import com.lamasatech.kioskhardware.products.NotSupportedMethodException

fun safeCall(resultView: TextView, action: () -> Any?) {
    try {
        val result = action()
        resultView.text = "Result: $result"
    } catch (e: NotSupportedMethodException) {
        resultView.text = "Not supported on this device"
    } catch (e: Exception) {
        resultView.text = "Error: ${e.message}"
    }
}
