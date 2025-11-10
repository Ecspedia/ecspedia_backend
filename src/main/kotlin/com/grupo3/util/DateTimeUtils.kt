package com.grupo3.util

import java.time.Instant
import java.time.format.DateTimeParseException

object DateTimeUtils {

    fun parseIsoInstant(value: String, fieldName: String): Instant =
        try {
            Instant.parse(value)
        } catch (ex: DateTimeParseException) {
            throw IllegalArgumentException("Invalid $fieldName format. Expected ISO-8601 but got: $value")
        }
}
