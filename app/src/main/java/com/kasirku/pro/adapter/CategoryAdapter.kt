package com.kasirku.pro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kasirku.pro.R
import com.kasirku.pro.data.entity.Category
import com.kasirku.pro.databinding.ItemCategoryChipBinding

class CategoryAdapter(
    private val onCategoryClick: (Category?) -> Unit
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    fun setSelectedPosition(position: Int) {
        val old = selectedPosition
        selectedPosition = position
        notifyItemChanged(old)
        notifyItemChanged(position)
    }

    inner class ViewHolder(private val binding: ItemCategoryChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean) {
            binding.tvCategoryName.text = category.name

            if (isSelected) {
                binding.chipContainer.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.primary)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.white)
                )
            } else {
                binding.chipContainer.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.white)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_primary)
                )
            }

            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    setSelectedPosition(pos)
                    onCategoryClick(if (pos == 0) null else category)
                }
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}
