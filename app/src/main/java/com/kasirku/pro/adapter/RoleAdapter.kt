package com.kasirku.pro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kasirku.pro.data.entity.Role
import com.kasirku.pro.databinding.ItemRoleRowBinding

class RoleAdapter(
    private val onEditClick: (Role) -> Unit,
    private val onDeleteClick: (Role) -> Unit
) : ListAdapter<Role, RoleAdapter.ViewHolder>(RoleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRoleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(role: Role) {
            binding.tvRoleName.text = role.name

            val permissions = mutableListOf<String>()
            if (role.canAccessPos) permissions.add("POS")
            if (role.canAccessItemManagement) permissions.add("Item")
            if (role.canAccessPurchase) permissions.add("Pembelian")
            if (role.canAccessReport) permissions.add("Laporan")
            if (role.canAccessCustomer) permissions.add("Customer")
            if (role.canAccessEmployee) permissions.add("Karyawan")
            if (role.canAccessDelivery) permissions.add("Pengiriman")
            if (role.canTakeDeliveryPhoto) permissions.add("Foto Bukti")
            if (role.canAccessSettings) permissions.add("Setting")
            if (role.canManageRoles) permissions.add("Kelola Role")

            binding.tvPermissions.text = permissions.joinToString(", ")
            binding.tvDefault.visibility = if (role.isDefault) android.view.View.VISIBLE else android.view.View.GONE

            binding.btnEdit.setOnClickListener { onEditClick(role) }
            binding.btnDelete.setOnClickListener { onDeleteClick(role) }
            binding.btnDelete.isEnabled = !role.isDefault
            binding.btnDelete.alpha = if (role.isDefault) 0.3f else 1.0f
        }
    }

    class RoleDiffCallback : DiffUtil.ItemCallback<Role>() {
        override fun areItemsTheSame(oldItem: Role, newItem: Role) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Role, newItem: Role) = oldItem == newItem
    }
}
