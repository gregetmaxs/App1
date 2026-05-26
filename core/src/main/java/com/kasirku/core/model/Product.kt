package com.kasirku.core.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val barcode: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val supplierId: String = "",
    val buyPrice: Long = 0L,
    val sellPrice: Long = 0L,
    val stock: Int = 0,
    val minStock: Int = 0,
    val unit: String = "pcs",
    val hasVariants: Boolean = false,
    val storeId: String = "",
    val isActive: Boolean = true,
    val variants: List<ProductVariant> = emptyList(),
    val categoryName: String = "",
    val supplierName: String = ""
)

data class ProductVariant(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val barcode: String = "",
    val buyPrice: Long = 0L,
    val sellPrice: Long = 0L,
    val stock: Int = 0,
    val isActive: Boolean = true
)
