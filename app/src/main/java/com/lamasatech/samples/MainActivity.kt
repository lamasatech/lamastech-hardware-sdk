package com.lamasatech.samples

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lamasatech.kioskhardware.products.DeviceManager
import com.lamasatech.kioskhardware.products.IDevice
import com.lamasatech.kioskhardware.products.Model
import com.lamasatech.samples.databinding.ActivityMainBinding
import com.lamasatech.samples.databinding.ItemCategoryBinding

class MainActivity : AppCompatActivity() {

    private val categories = listOf(
        Category("Device Info", DeviceInfoActivity::class.java),
        Category("Power", PowerActivity::class.java),
        Category("Display", DisplayActivity::class.java),
        Category("LED", LedActivity::class.java),
        Category("System UI", SystemUiActivity::class.java),
        Category("GPIO & Relay", GpioRelayActivity::class.java),
        Category("App Management", AppManagementActivity::class.java),
        Category("Network", NetworkActivity::class.java),
        Category("Ethernet", EthernetActivity::class.java),
        Category("Hardware", HardwareActivity::class.java),
        Category("RFID / QR Sample", ScannerActivity::class.java),
        Category("System", SystemActivity::class.java),
        Category("Settings", SettingsActivity::class.java),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.isNestedScrollingEnabled = false
        binding.recyclerView.adapter = CategoryAdapter(categories) {
            startActivity(Intent(this, it.activityClass))
        }

        binding.tvDeviceInfo.text = buildDeviceInfo(DeviceManager.instance?.device)
    }

    /** Every field is read independently so one unsupported method doesn't blank out the rest. */
    private fun buildDeviceInfo(device: IDevice?): CharSequence {
        val primary = ContextCompat.getColor(this, R.color.primary)
        val secondary = ContextCompat.getColor(this, R.color.text_secondary)
        val text = SpannableStringBuilder()

        fun section(title: String, body: () -> Unit) {
            if (text.isNotEmpty()) text.append("\n")
            val start = text.length
            text.append(title.uppercase()).append("\n")
            text.setSpan(StyleSpan(Typeface.BOLD), start, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(ForegroundColorSpan(primary), start, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(RelativeSizeSpan(1.05f), start, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            body()
        }

        fun line(label: String, value: String) {
            val labelStart = text.length
            text.append("  $label:  ")
            text.setSpan(ForegroundColorSpan(secondary), labelStart, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.append(value).append("\n")
        }

        section("Device") {
            line("Build.MODEL", Build.MODEL)
            line("Manufacturer / Brand", "${Build.MANUFACTURER} / ${Build.BRAND}")
            line("Android version", "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            line("Detected model type", safe { Model.type::class.simpleName })
            line("Hardware verified", safe { Model.verifyHardware() })
            line("15\" screen", safe { Model.is15Inch })
            line("AI acceleration", safe { Model.isSupportAI })
        }
        section("Capabilities") {
            line("Facial recognition", safe { Model.isSupportFacialRecognition })
            line("RFID (serial)", safe { Model.isSupportRfidSerial })
            line("RFID (keyboard)", safe { Model.isSupportRfidKeyboard })
            line("QR", safe { Model.isSupportQR })
            line("Temperature sensor", safe { Model.isSupportTemp })
            line("LED", safe { Model.isSupportLed })
            line("Printer", safe { Model.isSupportPrinter })
        }
        section("Network") {
            line("Current type", safe { device?.netGetCurrentNetType() })
            line("WiFi MAC", safe { device?.netGetMacAddress("wlan0") })
            line("WiFi RSSI level", safe { device?.netGetWifiRssi(5) })
            line("WiFi hotspot", safe { device?.netGetWifiAp() })
            line("Network priority", safe { device?.netGetNetworkPriority()?.joinToString() })
            line("Ethernet state", safe { device?.getEthernetState() })
            line("Ethernet IP", safe { device?.getEthIPAddress() })
            line("Ethernet MAC", safe { device?.getEthMacAddress() })
        }
        section("Identifiers") {
            line("Serial number", safe { device?.getSerialNumber() })
            line("IMEI", safe { device?.netGetImeiNumber() })
            line("ICCID", safe { device?.netGetIccidNumber() })
            line("IMSI", safe { device?.netGetImsiNumber() })
        }
        section("Hardware") {
            line("Blue LED supported", safe { device?.isSupportBlue() })
            line("Red LED supported", safe { device?.isSupportRed() })
            line("Relay I/O mode", safe { device?.getRelayIoMode() })
            line("Relay I/O value", safe { device?.getRelayIoValue() })
            line("Display rotation", safe { device?.getDisplayRotation(0) })
            line("LCD backlight enabled", safe { device?.getLcdBackLightEnable(0) })
        }
        section("System") {
            line("Default launcher", safe { device?.getDefaultLauncher(this@MainActivity) })
            line("Boot app", safe { device?.getSystemBootApp() })
            line("Daemon-guarded app", safe { device?.getDaemonsActivity() })
            line("NTP server", safe { device?.getNtpServer() })
            line("SD card path", safe { device?.getSDCardPath(this@MainActivity) })
        }
        section("Fun facts") {
            line("Uptime", safe { formatUptime(SystemClock.elapsedRealtime()) })
            line("Battery level", safe { "${batteryLevelPercent()}%" })
            line("Free storage", safe { "${gigabytes(freeStorageBytes())} GB free" })
            line("Total RAM", safe { "${gigabytes(totalRamBytes())} GB" })
        }

        return text
    }

    private fun formatUptime(millis: Long): String {
        val totalMinutes = millis / 60_000
        val days = totalMinutes / (24 * 60)
        val hours = (totalMinutes % (24 * 60)) / 60
        val minutes = totalMinutes % 60
        return buildString {
            if (days > 0) append("${days}d ")
            append("${hours}h ${minutes}m")
        }
    }

    private fun batteryLevelPercent(): Int {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun freeStorageBytes(): Long {
        val stat = StatFs(Environment.getDataDirectory().path)
        return stat.availableBlocksLong * stat.blockSizeLong
    }

    private fun totalRamBytes(): Long {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem
    }

    private fun gigabytes(bytes: Long): String =
        "%.1f".format(bytes / 1_000_000_000.0)

    /**
     * Runs [block] and renders the result as text, or "N/A" if unsupported.
     * Catches [Throwable], not just [Exception]: some vendor SDK calls that
     * don't exist on a given firmware build surface as [NoSuchMethodError]
     * (a [LinkageError]), which an `Exception`-only catch would miss.
     */
    private inline fun safe(block: () -> Any?): String = try {
        block()?.toString()?.takeIf { it.isNotBlank() } ?: "—"
    } catch (_: Throwable) {
        "N/A"
    }

    data class Category(val title: String, val activityClass: Class<*>)

    class CategoryAdapter(
        private val items: List<Category>,
        private val onClick: (Category) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.VH>() {

        class VH(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VH(binding)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            holder.binding.tvTitle.text = item.title
            holder.binding.root.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size
    }
}
