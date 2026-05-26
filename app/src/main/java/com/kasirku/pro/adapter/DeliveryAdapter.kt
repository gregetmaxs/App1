package com.kasirku.pro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kasirku.pro.R
import com.kasirku.pro.data.entity.DeliveryStatus
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.databinding.ItemDeliveryRowBinding
import com.kasirku.pro.util.CurrencyFormatter
import com.kasirku.pro.util.DateUtils

class DeliveryAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, DeliveryAdapter.ViewHolder>(DeliveryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeliveryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemDeliveryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvResi.text = transaction.resiNumber.ifEmpty { "#${transaction.id.take(8)}" }
            binding.tvCustomerName.text = transaction.customerName.ifEmpty { "-" }
            binding.tvAddress.text = transaction.deliveryAddress.ifEmpty { "-" }
            binding.tvAmount.text = CurrencyFormatter.formatSimple(transaction.totalAmount)
            binding.tvDate.text = DateUtils.formatDateTime(transaction.date)
            binding.tvDriver.text = "Supir: ${transaction.driverName.ifEmpty { "-" }}"
            binding.tvHelper.text = "Kenek: ${transaction.helperName.ifEmpty { "-" }}"

            val statusText = when (transaction.deliveryStatus) {
                DeliveryStatus.WAITING -> "Menunggu"
                DeliveryStatus.IN_PROGRESS -> "Dalam Perjalanan"
                DeliveryStatus.DELIVERED -> "Terkirim"
            }
            binding.tvStatus.text = statusText
            val color = when (transaction.deliveryStatus) {
                DeliveryStatus.WAITING -> R.color.warning
                DeliveryStatus.IN_PROGRESS -> R.color.info
                DeliveryStatus.DELIVERED -> R.color.success
            }
            binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, color))

            binding.root.setOnClickListener { onItemClick(transaction) }
        }
    }

    class DeliveryDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem == newItem
    }
}
