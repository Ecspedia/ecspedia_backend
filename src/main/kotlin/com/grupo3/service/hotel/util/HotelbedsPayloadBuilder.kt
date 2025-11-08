package com.grupo3.service.hotel.util

import com.grupo3.model.hotel.Location

object HotelbedsPayloadBuilder {

    fun buildAvailabilityPayload(
        location: Location,
        checkIn: String,
        checkOut: String,
        adults: Int
    ): String = """
        {
            "stay": {
                "checkIn": "$checkIn",
                "checkOut": "$checkOut"
            },
            "occupancies": [
                {
                    "rooms": 1,
                    "adults": $adults,
                    "children": 0
                }
            ],
            "geolocation": {
                "latitude": ${location.latitude},
                "longitude": ${location.longitude},
                "radius": 20,
                "unit": "km"
            }
        }
    """.trimIndent()
}
