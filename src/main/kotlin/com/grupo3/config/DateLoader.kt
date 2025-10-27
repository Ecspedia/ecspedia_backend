package com.grupo3.config

import com.grupo3.model.Hotel
import com.grupo3.repository.HotelRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

class DateLoader {

    @Component
    class DataLoader(private val hotelRepository: HotelRepository) : CommandLineRunner {

        override fun run(vararg args: String?) {
            // This runs after the application starts

            val hotels = listOf(
                Hotel(
                    name = "Hotel El Poblado Plaza",
                    location = "Medellín",
                    image = "https://expediastorage.s3.us-east-1.amazonaws.com/hotel_mock.avif",
                    isAvailable = true,
                    rating = 4.7,
                    reviewCount = 342,
                    pricePerNight = 330.0,
                    latitude = 6.2088,
                    longitude = -75.5673
                ),
                Hotel(
                    name = "Laureles Executive Hotel",
                    location = "Medellín",
                    image = "https://expediastorage.s3.us-east-1.amazonaws.com/img2.webp",
                    isAvailable = true,
                    rating = 4.5,
                    reviewCount = 256,
                    pricePerNight = 450.0,
                    latitude = 6.2443,
                    longitude = -75.5912
                ),
                Hotel(
                    name = "Centro Histórico Inn",
                    location = "Medellín",
                    image = "https://expediastorage.s3.us-east-1.amazonaws.com/img3.webp",
                    isAvailable = true,
                    rating = 4.3,
                    reviewCount = 189,
                    pricePerNight = 250.0,
                    latitude = 6.2518,
                    longitude = -75.5636
                ),
                Hotel(
                    name = "Envigado Boutique Hotel",
                        location = "Medellín",
                    image = "https://expediastorage.s3.us-east-1.amazonaws.com/img5.avif",
                    isAvailable = true,
                    rating = 4.8,
                    reviewCount = 423,
                    pricePerNight = 500.0,
                    latitude = 6.1701,
                    longitude = -75.5838
                )
            )

            hotelRepository.saveAll(hotels)

            println("✅ Database initialized with ${hotelRepository.count()} hotels")
        }
    }
}