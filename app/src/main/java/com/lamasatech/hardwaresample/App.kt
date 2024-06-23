package com.lamasatech.hardwaresample

import android.app.Application
import com.lamasatech.kioskhardware.products.DeviceManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DeviceManager.instance?.init(this)
    }
}