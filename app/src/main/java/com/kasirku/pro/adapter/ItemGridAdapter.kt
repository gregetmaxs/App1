package com.kasirku.pro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kasirku.pro.R
import com.kasirku.pro.data.entity.Item
import com.kasirku.pro.databinding.ItemGridCardBinding
import com.kasirku.pro.util.CurrencyFormatter
import java.io.File

class ItemGridAdapter(
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, ItemGridAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGridCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemGridCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.tvItemName.text = item.name
            binding.tvItemPrice.text = CurrencyFormatter.formatSimple(item.price)
            binding.tvItemStock.text = item.stock.toString()

            if (item.imagePath.isNotEmpty() && File(item.imagePath).exists()) {
                Glide.with(binding.root.context)
                    .load(File(item.imagePath))
                    .centerCrop()
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.ivItemImage)
            } else {
                binding.ivItemImage.setImageResource(R.drawable.ic_placeholder)
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
    }
}
