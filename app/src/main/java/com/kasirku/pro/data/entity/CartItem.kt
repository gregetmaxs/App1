package com.kasirku.pro.data.entity

data class CartItem(
    val item: Item,
    var quantity: Int = 1
) {
    val subtotal: Double get() = item.price * quantity
}
