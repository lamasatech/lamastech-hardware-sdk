package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivitySystemUiBinding
import com.lamasatech.samples.util.safeCall
import java.io.File

class SystemUiActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySystemUiBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "System UI"

        b.switchStatusBar.setOnCheckedChangeListener { _, isChecked ->
            safeCall(b.tvResult) { device?.setStatusBar(this, isChecked) }
        }
        b.btnEnableStatusBarDrag.setOnClickListener { safeCall(b.tvResult) { device?.setStatusBarDrag(true) } }
        b.btnDisableStatusBarDrag.setOnClickListener { safeCall(b.tvResult) { device?.setStatusBarDrag(false) } }
        b.switchNavBar.setOnCheckedChangeListener { _, isChecked ->
            safeCall(b.tvResult) { device?.setNavigationBar(isChecked) }
        }
        b.switchGestureBar.setOnCheckedChangeListener { _, isChecked ->
            safeCall(b.tvResult) { device?.setGestureBar(isChecked) }
        }
        b.btnHideStatusAndGestureBar.setOnClickListener {
            safeCall(b.tvResult) { device?.hideStatusBarAndDisableGestureBar(this) }
        }
        b.btnPullSystemLogs.setOnClickListener {
            safeCall(b.tvResult) {
                val file = File(filesDir, "pulled_syslog.txt")
                val code = device?.pullSystemLogs(file.absolutePath)
                "code=$code path=${file.absolutePath}"
            }
        }
    }
}
