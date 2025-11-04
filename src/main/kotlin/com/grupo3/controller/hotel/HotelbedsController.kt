package com.grupo3.controller.hotel

import com.grupo3.service.hotel.HotelbedsClient
import graphql.GraphQLException
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class HotelbedsController(
    private val hotelbedsClient: HotelbedsClient
) {

    @QueryMapping
    fun hotelbedsSampleSearch(): String {
        val payload = """
            {
                "stay": {
                    "checkIn": "2025-11-05",
                    "checkOut": "2025-11-10"
                },
                "occupancies": [
                    {
                        "rooms": 1,
                        "adults": 3,
                        "children": 0
                    }
                ],
                "geolocation": {
                    "latitude": 39.57119,
                    "longitude": 2.646633999999949,
                    "radius": 20,
                    "unit": "km"
                },
                "filter": {
                    "maxCategory": 4,
                    "minCategory": 2,
                    "maxRooms": 1,
                    "maxHotels": 2
                }
            }
        """.trimIndent()

        return try {
            hotelbedsClient.searchAvailability(payload)
        } catch (ex: IllegalStateException) {
            throw GraphQLException(ex.message ?: "Hotelbeds API call failed")
        }
    }
}
