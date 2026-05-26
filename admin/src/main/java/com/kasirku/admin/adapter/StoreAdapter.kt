package com.kasirku.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kasirku.admin.R
import com.kasirku.admin.data.entity.Store
import com.kasirku.admin.databinding.ItemStoreRowBinding
import com.kasirku.admin.util.DateUtils

class StoreAdapter(
    private val onClick: (Store) -> Unit = {}
) : ListAdapter<Store, StoreAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemStoreRowBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(a: Store, b: Store) = a.id == b.id
        override fun areContentsTheSame(a: Store, b: Store) = a == b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = getItem(position)
        with(holder.binding) {
            tvStoreName.text = store.name
            tvOwnerName.text = store.ownerName
            tvEmail.text = store.email
            tvPhone.text = store.phone
            tvLicenseKey.text = store.licenseKey
            tvDate.text = DateUtils.formatDate(store.createdAt)

            if (store.isActive) {
                tvStatus.text = "Aktif"
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.success))
            } else {
                tvStatus.text = "Nonaktif"
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.error))
            }

            root.setOnClickListener { onClick(store) }
        }
    }
}
