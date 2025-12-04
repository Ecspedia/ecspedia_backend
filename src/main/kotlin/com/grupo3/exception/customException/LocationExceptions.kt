package com.grupo3.exception.customException


sealed class LocationException(message: String) : RuntimeException(message)

class LocationNotFoundException(
    val code: String? = null,
    val city: String? = null
) : LocationException(
    buildString {
        append("Location not found")
        code?.let { append(" with code: $it") }
        city?.let { append(" for city: $it") }
    }
)

class LocationAlreadyExistsException(
    val city: String? = null,
    val country: String? = null

) : LocationException("Location with city $city and country $country already exists")