package com.grupo3.repository

import com.grupo3.model.Location
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface LocationRepository : JpaRepository<Location, String> {
    fun findByCode(code: String): Optional<Location>
    fun existsByCode(code: String): Boolean
    fun findByCity(city: String): Optional<Location>

    fun findByIsPopularTrueOrderByCityAsc(): List<Location>
    fun findByCityAndCountry(city: String, country: String): Location


}
