package com.lamasatech.samples

import android.app.Application
import com.lamasatech.kioskhardware.products.DeviceManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            DeviceManager.instance?.init(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
