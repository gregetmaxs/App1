package com.kasirku.admin.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    fun format(amount: Double): String = formatter.format(amount)

    fun formatSimple(amount: Double): String {
        return "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)}"
    }
}
