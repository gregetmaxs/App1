package com.kasirku.core.model

data class License(
    val id: String = "",
    val licenseKey: String = "",
    val storeId: String = "",
    val type: String = "",
    val status: String = "ACTIVE",
    val activatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L,
    val createdBy: String = "",
    val price: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isExpired: Boolean
        get() = status != "ACTIVE" ||
                (expiresAt > 0L && System.currentTimeMillis() > expiresAt)

    val isPermanent: Boolean
        get() = type == "PERMANENT" || expiresAt == 0L

    val remainingDays: Long
        get() {
            if (isPermanent) return Long.MAX_VALUE
            val remaining = expiresAt - System.currentTimeMillis()
            return if (remaining > 0) remaining / (24 * 60 * 60 * 1000) else 0
        }
}
