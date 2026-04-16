package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.samples.databinding.ActivityEthernetBinding
import com.lamasatech.samples.util.safeCall

class EthernetActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityEthernetBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "Ethernet"

        b.btnEnableEth.setOnClickListener { safeCall(b.tvResult) { device?.setEthernetState(true) } }
        b.btnDisableEth.setOnClickListener { safeCall(b.tvResult) { device?.setEthernetState(false) } }
        b.btnGetEthState.setOnClickListener { safeCall(b.tvResult) { device?.getEthernetState() } }
        b.btnGetEthIp.setOnClickListener { safeCall(b.tvResult) { device?.getEthIPAddress() } }
        b.btnGetEthMac.setOnClickListener { safeCall(b.tvResult) { device?.getEthMacAddress() } }
        b.btnSetStaticIp.setOnClickListener {
            safeCall(b.tvResult) {
                device?.setEthIPAddress(
                    b.etIp.text.toString(),
                    b.etMask.text.toString(),
                    b.etGateway.text.toString(),
                    b.etDns.text.toString()
                )
            }
        }
        b.btnEnableTimeSync.setOnClickListener { safeCall(b.tvResult) { device?.setTimeFromNetwork(true, this) } }
        b.btnDisableTimeSync.setOnClickListener { safeCall(b.tvResult) { device?.setTimeFromNetwork(false, this) } }
    }
}
