package com.kasirku.pro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kasirku.pro.data.entity.CartItem
import com.kasirku.pro.databinding.ItemCartRowBinding
import com.kasirku.pro.util.CurrencyFormatter

class CartAdapter(
    private val onQuantityChanged: (Int, Int) -> Unit,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var items: List<CartItem> = emptyList()

    fun submitList(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemCartRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.tvItemName.text = cartItem.item.name
            binding.tvItemPrice.text = CurrencyFormatter.formatSimple(cartItem.item.price)
            binding.tvQuantity.text = cartItem.quantity.toString()
            binding.tvSubtotal.text = CurrencyFormatter.formatSimple(cartItem.subtotal)

            binding.btnIncrease.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onQuantityChanged(pos, cartItem.quantity + 1)
                }
            }

            binding.btnDecrease.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onQuantityChanged(pos, cartItem.quantity - 1)
                }
            }

            binding.btnRemove.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onRemoveClick(pos)
                }
            }
        }
    }
}
