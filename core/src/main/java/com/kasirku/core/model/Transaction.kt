package com.kasirku.core.model

data class Transaction(
    val id: String = "",
    val transactionNumber: String = "",
    val cashierUserId: String = "",
    val cashierName: String = "",
    val customerName: String = "",
    val subtotal: Long = 0L,
    val ppnPercent: Double = 0.11,
    val ppnAmount: Long = 0L,
    val discount: Long = 0L,
    val total: Long = 0L,
    val paymentMethod: String = "CASH",
    val amountPaid: Long = 0L,
    val changeAmount: Long = 0L,
    val deliveryType: String = "SELF_PICKUP",
    val deliveryAddress: String = "",
    val driverUserId: String = "",
    val helperUserId: String = "",
    val deliveryStatus: String = "",
    val deliveryProofUrl: String = "",
    val resiNumber: String = "",
    val notes: String = "",
    val storeId: String = "",
    val items: List<TransactionItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class TransactionItem(
    val id: String = "",
    val transactionId: String = "",
    val productId: String = "",
    val variantId: String = "",
    val productName: String = "",
    val variantName: String = "",
    val quantity: Int = 0,
    val price: Long = 0L,
    val subtotal: Long = 0L
)
