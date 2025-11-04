package com.grupo3.exception

import java.util.Date

data class Violation(
    val field: String? = null,
    val message: String,
    val rejectedValue: String? = null
)

data class ErrorDetails(
    val message: String,
    val details: String,
    val localDateTime: Date,
    val code: String? = null,
    val status: Int? = null,
    val path: String? = null,
    val violations: List<Violation> = emptyList()
)
