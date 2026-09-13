package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivityNetworkBinding
import com.lamasatech.samples.util.safeCall

class NetworkActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityNetworkBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "Network"

        // Info
        b.btnGetNetType.setOnClickListener { safeCall(b.tvResult) { device?.netGetCurrentNetType(this) } }
        b.btnGetMacEth0.setOnClickListener { safeCall(b.tvResult) { device?.netGetMacAddress(this, "eth0") } }
        b.btnGetMacWlan0.setOnClickListener { safeCall(b.tvResult) { device?.netGetMacAddress(this, "wlan0") } }
        b.btnGetImei.setOnClickListener { safeCall(b.tvResult) { device?.netGetImeiNumber(this) } }
        b.btnGetIccid.setOnClickListener { safeCall(b.tvResult) { device?.netGetIccidNumber(this) } }
        b.btnGetImsi.setOnClickListener { safeCall(b.tvResult) { device?.netGetImsiNumber(this) } }
        b.btnGetWifiRssi.setOnClickListener { safeCall(b.tvResult) { device?.netGetWifiRssi(this, 5) } }

        // WiFi
        b.btnConnectWifi.setOnClickListener {
            val ssid = b.etSsid.text.toString()
            val password = b.etPassword.text.toString()
            safeCall(b.tvResult) { device?.netSetWifiConnect(this, ssid, password, 2, 0, null) }
        }
        b.btnEnableHotspot.setOnClickListener { safeCall(b.tvResult) { device?.netSetWifiAp(this, true) } }
        b.btnDisableHotspot.setOnClickListener { safeCall(b.tvResult) { device?.netSetWifiAp(this, false) } }
        b.btnGetHotspotStatus.setOnClickListener { safeCall(b.tvResult) { device?.netGetWifiAp(this) } }

        // Network Control
        b.btnEnableNetwork.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netSetNetWork(this, type, true) }
        }
        b.btnDisableNetwork.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netSetNetWork(this, type, false) }
        }
        b.btnGetNetworkMode.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netGetNetWorkModel(this, type) }
        }
        b.btnGetNetworkInfo.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netGetNetWorkInf(this, type) }
        }
    }
}
