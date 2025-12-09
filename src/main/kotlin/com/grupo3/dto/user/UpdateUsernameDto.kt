package com.grupo3.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUsernameDto(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String
)

