package com.grupo3.repository.hotel

import com.grupo3.model.hotel.Location
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface LocationRepository : JpaRepository<Location, String> {
    fun findByCode(code: String): Optional<Location>
    fun existsByCode(code: String): Boolean
    fun findByCity(city: String): Optional<Location>
}