package com.lamasatech.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lamasatech.kioskhardware.products.Device
import com.lamasatech.kioskhardware.products.DeviceManager

abstract class BaseActivity : AppCompatActivity() {
    val device: Device?
        get() = DeviceManager.instance?.device

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
