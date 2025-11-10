package com.grupo3.dto.hotel

data class HotelAccessibilityAttributesDto(
    val attributes: List<String>? = null,
    val showerChair: Boolean? = null,
    val entranceType: String? = null,
    val petFriendly: String? = null,
    val rampAngle: Int? = null,
    val rampLength: Int? = null,
    val entranceDoorWidth: Int? = null,
    val roomMaxGuestsNumber: Int? = null,
    val distanceFromTheElevatorToTheAccessibleRoom: Int? = null
)
