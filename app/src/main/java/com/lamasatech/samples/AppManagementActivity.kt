package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivityAppManagementBinding
import com.lamasatech.samples.util.safeCall

class AppManagementActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityAppManagementBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "App Management"

        b.btnSilentInstall.setOnClickListener {
            val path = b.etApkPath.text.toString()
            safeCall(b.tvResult) { device?.silentInstall(this, path) }
        }
        b.btnSilentUninstall.setOnClickListener {
            val pkg = b.etUninstallPkg.text.toString()
            safeCall(b.tvResult) { device?.silentUninstall(pkg) }
        }
        b.btnAllowUninstall.setOnClickListener {
            safeCall(b.tvResult) { device?.setAllowUninstall(true) }
        }
        b.btnBlockUninstall.setOnClickListener {
            safeCall(b.tvResult) { device?.setAllowUninstall(false) }
        }
        b.btnSetLauncher.setOnClickListener {
            val pkg = b.etLauncherPkg.text.toString()
            safeCall(b.tvResult) { device?.setDefaultLauncher(this, pkg) }
        }
        b.btnGetLauncher.setOnClickListener {
            safeCall(b.tvResult) { device?.getDefaultLauncher(this) }
        }
        b.btnSetBootApp.setOnClickListener {
            val pkg = b.etBootAppPkg.text.toString()
            safeCall(b.tvResult) { device?.setSystemBootApp(pkg) }
        }
        b.btnGetBootApp.setOnClickListener {
            safeCall(b.tvResult) { device?.getSystemBootApp() }
        }
        b.btnSetDaemon.setOnClickListener {
            val pkg = b.etDaemonPkg.text.toString()
            safeCall(b.tvResult) { device?.setDaemonsActivity(pkg, 5000L, true) }
        }
        b.btnGetDaemon.setOnClickListener {
            safeCall(b.tvResult) { device?.getDaemonsActivity() }
        }
    }
}
