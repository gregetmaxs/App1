package com.kasirku.pro.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object ResiGenerator {

    private val counter = AtomicInteger(0)
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    fun generate(): String {
        val date = dateFormat.format(Date())
        val seq = counter.incrementAndGet()
        return "KKP-$date-${String.format("%04d", seq)}"
    }
}
