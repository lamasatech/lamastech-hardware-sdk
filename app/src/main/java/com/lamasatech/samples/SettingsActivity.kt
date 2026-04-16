package com.lamasatech.samples

import android.os.Bundle
import com.lamasatech.kioskhardware.enterprise_agent.Result
import com.lamasatech.samples.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        title = "Settings"

        // System Settings
        b.btnSysPutInt.setOnClickListener {
            val name = b.etSysName.text.toString()
            val value = b.etSysValue.text.toString().toIntOrNull() ?: 0
            device?.sysSettingPutInt(name, value) { result ->
                runOnUiThread { showResult(b.tvResult, result) }
            }
        }
        b.btnSysGetInt.setOnClickListener {
            val name = b.etSysName.text.toString()
            device?.sysSettingGetInt(name) { result ->
                runOnUiThread { showResult(b.tvResult, result) }
            }
        }

        // Secure Settings
        b.btnSecurePutInt.setOnClickListener {
            val name = b.etSecureName.text.toString()
            val value = b.etSecureValue.text.toString().toIntOrNull() ?: 0
            device?.secureSettingPutInt(name, value) { result ->
                runOnUiThread { showResult(b.tvResult, result) }
            }
        }
        b.btnSecureGetInt.setOnClickListener {
            val name = b.etSecureName.text.toString()
            device?.secureSettingGetInt(name) { result ->
                runOnUiThread { showResult(b.tvResult, result) }
            }
        }

        // Global Settings
        b.btnGlobalPutInt.setOnClickListener {
            val name = b.etGlobalName.text.toString()
            val value = b.etGlobalValue.text.toString().toIntOrNull() ?: 0
            device?.globalSettingPutInt(name, value) { result ->
                runOnUiThread { showResult(b.tvResult, result) }
            }
        }
        b.btnGlobalGetInt.setOnClickListener {
            val name = b.etGlobalName.text.toString()
            device?.globalSettingGetInt(name) { result ->
                runOnUiThread { showResult(b.tvResult, result) }
            }
        }

        // Permissions
        b.btnGrantAccessibility.setOnClickListener {
            val componentName = b.etComponentName.text.toString()
            device?.grantAccessibilityPermission(componentName) { result ->
                runOnUiThread {
                    when (result) {
                        is Result.Success -> b.tvResult.text = "Result: ${result.data}"
                        is Result.Failure -> b.tvResult.text = "Error: ${result.exception}"
                    }
                }
            }
        }
    }

    private fun <T> showResult(tv: android.widget.TextView, result: Result<T>) {
        when (result) {
            is Result.Success -> tv.text = "Result: ${result.data}"
            is Result.Failure -> tv.text = "Error: ${result.exception}"
        }
    }
}
