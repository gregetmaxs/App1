package com.kasirku.pro.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    private val indonesianLocale = Locale("id", "ID")
    private val formatter = NumberFormat.getCurrencyInstance(indonesianLocale).apply {
        maximumFractionDigits = 0
    }

    fun format(amount: Double): String {
        return formatter.format(amount)
    }

    fun formatSimple(amount: Double): String {
        val nf = NumberFormat.getNumberInstance(indonesianLocale)
        nf.maximumFractionDigits = 0
        return "Rp ${nf.format(amount)}"
    }
}
