package com.kasirku.core.util

object Constants {
    const val DB_NAME = "kasirku_database"
    const val FIRESTORE_LICENSES = "licenses"
    const val FIRESTORE_STORES = "stores"
    const val FIRESTORE_USERS = "users"
    const val FIRESTORE_PRODUCTS = "products"
    const val FIRESTORE_CATEGORIES = "categories"
    const val FIRESTORE_TRANSACTIONS = "transactions"
    const val FIRESTORE_SUPPLIERS = "suppliers"
    const val FIRESTORE_LICENSE_PRICES = "license_prices"

    const val PPN_DEFAULT = 0.11 // 11%

    val LICENSE_DURATIONS = mapOf(
        "3_DAYS" to 3L,
        "7_DAYS" to 7L,
        "14_DAYS" to 14L,
        "30_DAYS" to 30L,
        "1_YEAR" to 365L,
        "2_YEARS" to 730L,
        "PERMANENT" to -1L
    )

    val LICENSE_LABELS = mapOf(
        "3_DAYS" to "3 Hari",
        "7_DAYS" to "7 Hari",
        "14_DAYS" to "14 Hari",
        "30_DAYS" to "30 Hari",
        "1_YEAR" to "1 Tahun",
        "2_YEARS" to "2 Tahun",
        "PERMANENT" to "Permanent"
    )
}
