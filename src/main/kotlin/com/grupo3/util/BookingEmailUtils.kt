package com.grupo3.util

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object BookingEmailUtils {
    private val EMAIL_DATE_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm 'UTC'")
            .withZone(ZoneId.of("UTC"))

    fun formatInstantForEmail(instant: Instant): String =
        EMAIL_DATE_FORMATTER.format(instant)

    fun formatPrice(price: Long?, currency: String?): String? =
        price?.let {
            val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(it)
            if (!currency.isNullOrBlank()) "$currency $formattedPrice" else formattedPrice
        }

    fun buildGuestName(firstName: String, lastName: String): String =
        listOf(firstName.trim(), lastName.trim())
            .filter { it.isNotEmpty() }
            .joinToString(" ")
            .ifBlank { "Primary guest" }
}
