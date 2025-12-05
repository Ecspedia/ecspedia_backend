package com.grupo3.exception.customException

class HotelApiException(
    val statusCode: Int,
    val errorBody: String,
    message: String = "Hotel API error: $statusCode"
) : RuntimeException(message)