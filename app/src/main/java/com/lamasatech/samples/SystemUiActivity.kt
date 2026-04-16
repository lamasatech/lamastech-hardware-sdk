package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivitySystemUiBinding
import com.lamasatech.samples.util.safeCall

class SystemUiActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySystemUiBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "System UI"

        b.btnShowStatusBar.setOnClickListener { safeCall(b.tvResult) { device?.setStatusBar(this, true) } }
        b.btnHideStatusBar.setOnClickListener { safeCall(b.tvResult) { device?.setStatusBar(this, false) } }
        b.btnEnableStatusBarDrag.setOnClickListener { safeCall(b.tvResult) { device?.setStatusBarDrag(true) } }
        b.btnDisableStatusBarDrag.setOnClickListener { safeCall(b.tvResult) { device?.setStatusBarDrag(false) } }
        b.btnShowNavBar.setOnClickListener { safeCall(b.tvResult) { device?.setNavigationBar(true) } }
        b.btnHideNavBar.setOnClickListener { safeCall(b.tvResult) { device?.setNavigationBar(false) } }
        b.btnEnableGesture.setOnClickListener { safeCall(b.tvResult) { device?.setGestureBar(true) } }
        b.btnDisableGesture.setOnClickListener { safeCall(b.tvResult) { device?.setGestureBar(false) } }
    }
}
