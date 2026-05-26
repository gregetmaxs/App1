package com.kasirku.pro.util

import java.util.Calendar

object GreetingGenerator {

    private val morningGreetings = listOf(
        "Selamat Pagi, Boss!",
        "Pagi yang cerah, Boss!",
        "Met Pagi, Boss!",
        "Morning, Boss!",
        "Pagi semangat, Boss!"
    )

    private val afternoonGreetings = listOf(
        "Selamat Siang, Boss!",
        "Semangat siang, Boss!",
        "Siang produktif, Boss!",
        "Met Siang, Boss!",
        "Tetap semangat, Boss!"
    )

    private val eveningGreetings = listOf(
        "Selamat Sore, Boss!",
        "Sore produktif, Boss!",
        "Met Sore, Boss!",
        "Good Evening, Boss!",
        "Sore yang baik, Boss!"
    )

    private val nightGreetings = listOf(
        "Selamat Malam, Boss!",
        "Lembur nih, Boss?",
        "Met Malam, Boss!",
        "Masih semangat, Boss!",
        "Malam produktif, Boss!"
    )

    fun generate(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetings = when {
            hour in 4..10 -> morningGreetings
            hour in 11..14 -> afternoonGreetings
            hour in 15..18 -> eveningGreetings
            else -> nightGreetings
        }
        return greetings.random()
    }
}
