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
        b.btnGetNetType.setOnClickListener { safeCall(b.tvResult) { device?.netGetCurrentNetType() } }
        b.btnGetMacEth0.setOnClickListener { safeCall(b.tvResult) { device?.netGetMacAddress("eth0") } }
        b.btnGetMacWlan0.setOnClickListener { safeCall(b.tvResult) { device?.netGetMacAddress("wlan0") } }
        b.btnGetImei.setOnClickListener { safeCall(b.tvResult) { device?.netGetImeiNumber() } }
        b.btnGetIccid.setOnClickListener { safeCall(b.tvResult) { device?.netGetIccidNumber() } }
        b.btnGetImsi.setOnClickListener { safeCall(b.tvResult) { device?.netGetImsiNumber() } }
        b.btnGetWifiRssi.setOnClickListener { safeCall(b.tvResult) { device?.netGetWifiRssi(5) } }

        // WiFi
        b.btnConnectWifi.setOnClickListener {
            val ssid = b.etSsid.text.toString()
            val password = b.etPassword.text.toString()
            safeCall(b.tvResult) { device?.netSetWifiConnect(ssid, password, 2, 0, null) }
        }
        b.btnEnableHotspot.setOnClickListener { safeCall(b.tvResult) { device?.netSetWifiAp(true) } }
        b.btnDisableHotspot.setOnClickListener { safeCall(b.tvResult) { device?.netSetWifiAp(false) } }
        b.btnGetHotspotStatus.setOnClickListener { safeCall(b.tvResult) { device?.netGetWifiAp() } }

        // Network Control
        b.btnEnableNetwork.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netSetNetWork(type, true) }
        }
        b.btnDisableNetwork.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netSetNetWork(type, false) }
        }
        b.btnGetNetworkMode.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netGetNetWorkModel(type) }
        }
        b.btnGetNetworkInfo.setOnClickListener {
            val type = b.etNetType.text.toString().ifEmpty { "eth0" }
            safeCall(b.tvResult) { device?.netGetNetWorkInf(type) }
        }
    }
}
