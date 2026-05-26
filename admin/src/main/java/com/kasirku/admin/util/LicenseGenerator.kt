package com.kasirku.admin.util

object LicenseGenerator {
    private val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun generate(): String {
        val part1 = randomPart(4)
        val part2 = randomPart(4)
        val part3 = randomPart(4)
        return "KKP-$part1-$part2-$part3"
    }

    private fun randomPart(length: Int): String {
        return (1..length).map { chars.random() }.joinToString("")
    }
}
