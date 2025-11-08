package com.grupo3.repository.hotel

import com.grupo3.model.hotel.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface LocationRepository : JpaRepository<Location, String> {
    fun findByCode(code: String): Optional<Location>
    fun existsByCode(code: String): Boolean
    fun findByCity(city: String): Optional<Location>

    @Query(
        """
      SELECT l
      FROM Location l
      WHERE l.isPopular = true
      ORDER BY l.city ASC
      """
    )
    fun findByPopularDestinations(): List<Location>

}