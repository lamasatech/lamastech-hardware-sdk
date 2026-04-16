package com.lamasatech.samples

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        Category("System", SystemActivity::class.java),
        Category("Settings", SettingsActivity::class.java),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val modelType = Model.type::class.simpleName
            binding.tvDeviceInfo.text = "Model: ${Build.MODEL} | Type: $modelType"
        } catch (e: Exception) {
            binding.tvDeviceInfo.text = "Device: ${Build.MODEL} (SDK not initialized)"
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = CategoryAdapter(categories) {
            startActivity(Intent(this, it.activityClass))
        }
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
