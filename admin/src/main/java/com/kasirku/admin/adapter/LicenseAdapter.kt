package com.kasirku.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kasirku.admin.R
import com.kasirku.admin.data.entity.License
import com.kasirku.admin.databinding.ItemLicenseRowBinding
import com.kasirku.admin.util.CurrencyFormatter
import com.kasirku.admin.util.DateUtils

class LicenseAdapter(
    private val onToggle: (License) -> Unit,
    private val onClick: (License) -> Unit = {}
) : ListAdapter<License, LicenseAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemLicenseRowBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<License>() {
        override fun areItemsTheSame(a: License, b: License) = a.id == b.id
        override fun areContentsTheSame(a: License, b: License) = a == b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLicenseRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val license = getItem(position)
        with(holder.binding) {
            tvLicenseKey.text = license.licenseKey
            tvStoreName.text = license.storeName
            tvOwnerName.text = license.ownerName
            tvPrice.text = CurrencyFormatter.formatSimple(license.price)
            tvDate.text = DateUtils.formatDate(license.createdAt)

            if (license.isActive) {
                tvStatus.text = "Aktif"
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.success))
                btnToggle.text = "Nonaktifkan"
            } else {
                tvStatus.text = "Nonaktif"
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.error))
                btnToggle.text = "Aktifkan"
            }

            btnToggle.setOnClickListener { onToggle(license) }
            root.setOnClickListener { onClick(license) }
        }
    }
}
