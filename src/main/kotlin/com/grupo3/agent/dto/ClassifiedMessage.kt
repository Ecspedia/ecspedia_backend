package com.grupo3.agent.dto

import com.grupo3.agent.enums.UserIntent

data class ClassifiedMessage(
    val originalMessage: String,
    val userId: String?,
    val hotelId: String?,
    val query: String,
    val intent: UserIntent
)