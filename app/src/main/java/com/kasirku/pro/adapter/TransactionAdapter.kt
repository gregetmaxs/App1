package com.kasirku.pro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kasirku.pro.R
import com.kasirku.pro.data.entity.DeliveryStatus
import com.kasirku.pro.data.entity.DeliveryType
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.data.entity.TransactionType
import com.kasirku.pro.databinding.ItemTransactionRowBinding
import com.kasirku.pro.util.CurrencyFormatter
import com.kasirku.pro.util.DateUtils

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTransactionRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTransactionId.text = if (transaction.resiNumber.isNotEmpty()) {
                transaction.resiNumber
            } else {
                "#${transaction.id.take(8)}"
            }
            binding.tvCustomerName.text = transaction.customerName.ifEmpty { "-" }
            binding.tvDate.text = DateUtils.formatDateTime(transaction.date)
            binding.tvAmount.text = CurrencyFormatter.formatSimple(transaction.totalAmount)
            binding.tvCashier.text = transaction.userName

            val typeText = if (transaction.type == TransactionType.SALE) "Penjualan" else "Pembelian"
            binding.tvType.text = typeText

            if (transaction.deliveryType == DeliveryType.DELIVERY) {
                val statusText = when (transaction.deliveryStatus) {
                    DeliveryStatus.WAITING -> "Menunggu"
                    DeliveryStatus.IN_PROGRESS -> "Dalam Perjalanan"
                    DeliveryStatus.DELIVERED -> "Terkirim"
                }
                binding.tvDeliveryStatus.text = "Kirim: $statusText"
                binding.tvDeliveryStatus.visibility = android.view.View.VISIBLE
                val color = when (transaction.deliveryStatus) {
                    DeliveryStatus.WAITING -> R.color.warning
                    DeliveryStatus.IN_PROGRESS -> R.color.info
                    DeliveryStatus.DELIVERED -> R.color.success
                }
                binding.tvDeliveryStatus.setTextColor(ContextCompat.getColor(binding.root.context, color))
            } else {
                binding.tvDeliveryStatus.text = "Ambil Sendiri"
                binding.tvDeliveryStatus.visibility = android.view.View.VISIBLE
            }

            binding.root.setOnClickListener { onItemClick(transaction) }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem == newItem
    }
}
