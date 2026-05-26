package com.kasirku.core.model

data class PriceHistory(
    val id: String = "",
    val productId: String = "",
    val variantId: String = "",
    val oldBuyPrice: Long = 0L,
    val newBuyPrice: Long = 0L,
    val oldSellPrice: Long = 0L,
    val newSellPrice: Long = 0L,
    val changedByUserId: String = "",
    val changedByName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
