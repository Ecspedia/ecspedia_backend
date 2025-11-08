package com.grupo3.dto.hotel.hotelbeds

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsAvailabilityResponse(
    val hotels: HotelbedsHotelCollection? = null,
    val auditData: HotelbedsAuditData? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsHotelCollection(
    val total: Int? = null,
    val hotels: List<HotelbedsHotel>? = null,
    val checkIn: String? = null,
    val checkOut: String? = null,
    val currency: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsHotel(
    val code: Long? = null,
    val name: String? = null,
    val destinationCode: String? = null,
    val destinationName: String? = null,
    val categoryCode: String? = null,
    val categoryName: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val minRate: String? = null,
    val maxRate: String? = null,
    val rooms: List<HotelbedsRoom>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsRoom(
    val code: String? = null,
    val name: String? = null,
    val rates: List<HotelbedsRate>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsRate(
    val rateKey: String? = null,
    val rateType: String? = null,
    val rateClass: String? = null,
    val net: String? = null,
    val currency: String? = null,
    val rooms: Int? = null,
    val adults: Int? = null,
    val children: Int? = null,
    val boardCode: String? = null,
    val boardName: String? = null,
    val paymentType: String? = null,
    val packaging: Boolean? = null,
    val allotment: Int? = null,
    val cancellationPolicies: List<HotelbedsCancellationPolicy>? = null,
    val offers: List<HotelbedsOffer>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsCancellationPolicy(
    val from: String? = null,
    val amount: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsOffer(
    val code: String? = null,
    val name: String? = null,
    val amount: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelbedsAuditData(
    val token: String? = null,
    val processTime: String? = null,
    val timestamp: String? = null,
    val environment: String? = null
)
