package com.kasirku.core.model

data class CartItem(
    val product: Product,
    val variant: ProductVariant? = null,
    val quantity: Int = 1
) {
    val price: Long
        get() = variant?.sellPrice ?: product.sellPrice

    val subtotal: Long
        get() = price * quantity

    val displayName: String
        get() = if (variant != null) "${product.name} - ${variant.name}" else product.name
}
