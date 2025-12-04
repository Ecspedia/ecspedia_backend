# Ecspedia Backend - Comprehensive Analysis & Improvement Recommendations

## Executive Summary

This document provides a detailed analysis of the Ecspedia Spring Boot application, identifying security vulnerabilities, architectural issues, performance bottlenecks, and code quality concerns. The application is a modern hotel booking platform using Spring Boot 3, Kotlin, GraphQL, and PostgreSQL.

**Total Issues Found:** 30+ actionable improvements
**Critical Issues:** 4
**High Priority:** 5
**Medium Priority:** 8+

---

## 1. SECURITY ISSUES ⚠️

### 1.1 Hardcoded Credentials & Secrets (CRITICAL)

**Location:** `src/main/resources/application.properties:5, 35`

**Issues:**
```properties
spring.datasource.password=password                    # Line 5
jwt.secret=secret                                       # Line 35
```

**Impact:**
- Database credentials exposed in version control
- JWT secret is trivially weak ("secret")
- Anyone with repo access can access production database
- Credentials logged in Spring Boot startup messages

**Recommendation:**
```properties
# application.properties - REMOVE ALL SECRETS
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

Add to `.env`:
```bash
DB_PASSWORD=<strong-random-password>
JWT_SECRET=<strong-random-secret-min-32-chars>
LITEAPI_API_KEY=<key>
GEMINI_API_KEY=<key>
EMAIL_USERNAME=<email>
EMAIL_PASSWORD=<password>
```

**Status:** Not implemented ❌

---

### 1.2 Weak Authentication Exception Handling (HIGH)

**Location:** `src/main/kotlin/com/grupo3/service/AuthService.kt:10, 23, 26`

**Code:**
```kotlin
import kotlin.RuntimeException  // Unnecessary import

fun authenticate(authRequest: AuthRequestDto): AuthResponseDto {
    val user = userRepository.findByUsername(authRequest.username)
        ?: userRepository.findByEmail(authRequest.username)
        ?: throw RuntimeException("User not found")  // Generic exception

    if (!passwordEncoder.matches(authRequest.password, user.password)) {
        throw RuntimeException("Invalid credentials")  // Generic exception
    }
    // ...
}
```

**Issues:**
- Uses generic `RuntimeException` instead of Spring Security exceptions
- Message "User not found" leaks information about registered users
- No logging of failed authentication attempts
- Not caught specifically by security handlers

**Recommendation:**
```kotlin
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.slf4j.LoggerFactory

@Service
class AuthService(
    // ... constructor params
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional(readOnly = true)
    fun authenticate(authRequest: AuthRequestDto): AuthResponseDto {
        val user = userRepository.findByUsername(authRequest.username)
            ?: userRepository.findByEmail(authRequest.username)
            ?: run {
                logger.warn("Authentication attempt for non-existent user: ${authRequest.username}")
                throw UsernameNotFoundException("Invalid credentials")
            }

        if (!passwordEncoder.matches(authRequest.password, user.password)) {
            logger.warn("Failed authentication for user: ${user.username}")
            throw BadCredentialsException("Invalid credentials")
        }

        logger.info("User authenticated: ${user.username}")
        val token = jwtTokenService.generateToken(user)
        return AuthResponseDto(token)
    }
}
```

**Status:** Not implemented ❌

---

### 1.3 CORS Configuration Over-Permissive (HIGH)

**Location:** `src/main/kotlin/com/grupo3/security/SecurityConfig.kt:17, 44` and `src/main/kotlin/com/grupo3/controller/hotel/HotelController.kt:17`

**Issues:**

**A. SecurityConfig.kt (Line 44):**
```kotlin
config.allowedHeaders = listOf("*")  // Allows ALL headers
```

**B. HotelController.kt (Line 17):**
```kotlin
@CrossOrigin(origins = ["*"])  // Redundant with SecurityConfig
class HotelController(...)
```

**Problems:**
- `allowedHeaders = "*"` allows any header (including authorization headers from malicious origins)
- `@CrossOrigin(origins = ["*"])` duplicates SecurityConfig settings and is overly permissive
- Violates CORS security model
- No credential handling specification

**Recommendation:**
```kotlin
// SecurityConfig.kt
@Bean
fun corsConfigurationSource(): CorsConfigurationSource {
    val allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS")
        ?: "https://ecspedia.shop,https://dev.ecspedia.shop,http://localhost:3000"
    val allowedOrigins = allowedOriginsEnv.split(",").map { it.trim() }

    val config = CorsConfiguration()
    config.allowedOrigins = allowedOrigins
    config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
    // Explicitly list allowed headers instead of "*"
    config.allowedHeaders = listOf(
        "Content-Type",
        "Authorization",
        "X-Requested-With",
        "Accept"
    )
    config.exposedHeaders = listOf("Authorization")
    config.allowCredentials = true
    config.maxAge = 3600L  // 1 hour

    val source = UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", config)
    return source
}

// HotelController.kt - REMOVE @CrossOrigin entirely
@Controller
@Validated
class HotelController(private val hotelService: HotelService) {
    // ... rest of code
}
```

**Status:** Not implemented ❌

---

### 1.4 JWT Token Service Missing Security Claims (MEDIUM)

**Location:** `src/main/kotlin/com/grupo3/security/jwt/JwtTokenService.kt:21-28`

**Code:**
```kotlin
fun generateToken(user: User): String {
    return JWT.create()
        .withSubject(user.username)
        .withClaim("userId", user.id)
        .withIssuedAt(Date())
        .withExpiresAt(Date(System.currentTimeMillis() + expiration))
        .sign(algorithm)
}
```

**Issues:**
- Missing `issuer` claim (should be your app name)
- Missing `audience` claim (should be "api" or similar)
- No token type specified
- Verifier doesn't validate all claims

**Recommendation:**
```kotlin
@Service
class JwtTokenService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long,
    @Value("\${app.name:ecspedia}") private val appName: String
) {
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(appName)
        .withAudience("api")
        .build()

    fun generateToken(user: User): String {
        return JWT.create()
            .withIssuer(appName)
            .withAudience("api")
            .withSubject(user.username)
            .withClaim("userId", user.id)
            .withClaim("email", user.email)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(algorithm)
    }

    fun validateToken(token: String): DecodedJWT? {
        return try {
            verifier.verify(token)
        } catch (verificationEx: JWTVerificationException) {
            logger.warn("JWT verification failed: ${verificationEx.message}")
            null
        }
    }
}
```

**Status:** Not implemented ❌

---

### 1.5 No Rate Limiting (HIGH)

**Issue:** API endpoints are unprotected from brute force and DoS attacks.

**Recommendation:** Add Spring Cloud Config with rate limiting:

Add dependency to `build.gradle.kts`:
```gradle
implementation("io.github.bucket4j:bucket4j-spring-boot-starter:7.6.0")
```

Create `src/main/kotlin/com/grupo3/config/RateLimitConfig.kt`:
```kotlin
package com.grupo3.config

import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import io.github.bucket4j.Bandwidth
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RateLimitConfig {

    @Bean
    fun authBucket(): Bucket {
        val limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)))
        return Bucket4j.builder()
            .addLimit(limit)
            .build()
    }

    @Bean
    fun apiBucket(): Bucket {
        val limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)))
        return Bucket4j.builder()
            .addLimit(limit)
            .build()
    }
}
```

Create interceptor to apply rate limiting to auth endpoints.

**Status:** Not implemented ❌

---

## 2. ARCHITECTURAL & DESIGN ISSUES

### 2.1 Missing Pagination on Hotel Queries (HIGH)

**Location:** `src/main/kotlin/com/grupo3/service/hotel/HotelService.kt:59-63`

**Code:**
```kotlin
fun getAllHotels(): List<HotelResponseDto> =
    hotelRepository.findAll().map { HotelMapper.toResponseDto(it) }

fun getAllPartialHotels(): List<HotelPartialResponseDto> =
    hotelRepository.findAll().map { HotelMapper.toPartialResponseDto(it) }
```

**Issues:**
- Loads ALL hotels into memory (no limit)
- If you have 100k+ hotels, causes:
  - Out of memory errors
  - Long API response times
  - High database load
- No sorting options
- Not scalable

**Recommendation:**

Update repository:
```kotlin
// HotelRepository.kt
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface HotelRepository : JpaRepository<Hotel, String> {
    fun findTop10ByIsAvailableTrueOrderByRatingDesc(): List<HotelResponseDto>

    // Add pagination
    fun findAll(pageable: Pageable): Page<Hotel>

    fun findByLocation(location: String, pageable: Pageable): Page<Hotel>
}
```

Update service:
```kotlin
// HotelService.kt
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

fun getAllHotels(page: Int = 0, size: Int = 20): Page<HotelResponseDto> {
    require(page >= 0) { "Page must be >= 0" }
    require(size in 1..100) { "Page size must be between 1 and 100" }

    val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("rating")))
    return hotelRepository.findAll(pageable)
        .map { HotelMapper.toResponseDto(it) }
}

fun searchHotelsByLocation(
    location: String,
    page: Int = 0,
    size: Int = 20
): Page<HotelResponseDto> {
    val pageable = PageRequest.of(page, size, Sort.by("rating").descending())
    return hotelRepository.findByLocation(location, pageable)
        .map { it.toResponseDto() }
}
```

Update GraphQL schema (schema.graphqls):
```graphql
type Query {
    hotels(page: Int!, size: Int!): HotelPage!
    hotelsByLocation(location: String!, page: Int!, size: Int!): HotelPage!
}

type HotelPage {
    content: [Hotel!]!
    totalElements: Long!
    totalPages: Int!
    currentPage: Int!
    hasNext: Boolean!
    hasPrevious: Boolean!
}
```

**Status:** Not implemented ❌

---

### 2.2 Cache Invalidation Missing (HIGH)

**Location:** `src/main/kotlin/com/grupo3/service/hotel/HotelService.kt:27, 48-52`

**Code:**
```kotlin
@Cacheable(value = ["hotelsByLocation"], key = "#locationQuery.toLowerCase()")
fun searchHotelsByLocation(locationQuery: String): List<HotelResponseDto> {
    // ... cached search
}

@Transactional
fun saveHotel(hotelCreateDto: HotelCreateDto): HotelResponseDto {
    val hotel = HotelMapper.toEntity(hotelCreateDto)
    val savedHotel = hotelRepository.save(hotel)
    return HotelMapper.toResponseDto(savedHotel)
    // Cache NOT invalidated! Old data returned to users
}
```

**Issues:**
- When a hotel is created/updated, cache is not cleared
- Clients see stale hotel data indefinitely
- No cache eviction strategy

**Recommendation:**
```kotlin
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable

@Service
class HotelService(
    private val locationService: LocationService,
    private val hotelClient: HotelClient,
    private val hotelRepository: HotelRepository
) {

    @Cacheable(value = ["hotelsByLocation"], key = "#locationQuery.toLowerCase()")
    fun searchHotelsByLocation(locationQuery: String): List<HotelResponseDto> {
        val location = locationService.getLocationByCity(locationQuery)
        val json = hotelClient.searchHotels(LocationMapper.toEntity(location))
        val response: LiteApiSearchResponse = mapper.readValue(json)
        if (response.data.isEmpty()) {
            return emptyList()
        }
        return response.data.map { it.toResponseDto() }
    }

    @Transactional
    @CacheEvict(value = ["hotelsByLocation"], allEntries = true)  // Clear ALL location caches
    fun saveHotel(hotelCreateDto: HotelCreateDto): HotelResponseDto {
        val hotel = HotelMapper.toEntity(hotelCreateDto)
        val savedHotel = hotelRepository.save(hotel)
        return HotelMapper.toResponseDto(savedHotel)
    }

    @Transactional
    @CacheEvict(value = ["hotelsByLocation"], allEntries = true)
    fun updateHotel(id: String, hotelDto: HotelCreateDto): HotelResponseDto {
        val hotel = hotelRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Hotel not found") }
        // update fields
        val updated = hotelRepository.save(hotel)
        return HotelMapper.toResponseDto(updated)
    }
}
```

**Status:** Not implemented ❌

---

### 2.3 Inconsistent API Design (MEDIUM)

**Location:** Multiple controllers: REST (`/api/**`) and GraphQL (`/graphql`)

**Issues:**
- Hotel endpoints use GraphQL but also exposed via REST patterns
- No API versioning (`/v1/`, `/v2/`)
- Inconsistent naming conventions between REST and GraphQL
- Hard to maintain two different API styles
- Unclear which one is the primary API

**Recommendation:**

**Option A: Go GraphQL-only (recommended)**
- Remove REST endpoints
- Use GraphQL for all data queries
- Simpler to maintain
- Better for frontend performance (no over-fetching)

**Option B: REST with versioning**
```
/api/v1/hotels          (Get hotels)
/api/v1/hotels/{id}     (Get hotel by ID)
/api/v1/bookings        (Get bookings)
```

**Current state:** Unclear which approach is preferred ❌

---

### 2.4 Async Email in Transaction (MEDIUM)

**Location:** `src/main/kotlin/com/grupo3/service/booking/BookingService.kt:27, 65`

**Code:**
```kotlin
@Transactional
fun createBooking(...): BookingResponseDto {
    val booking = Booking(...)
    val savedBooking = bookingRepository.save(booking)

    // Email sent OUTSIDE transaction - already committed!
    CompletableFuture.runAsync { notifyUserOfBooking(user, savedBooking) }

    return BookingMapper.toResponseDto(savedBooking)
}
```

**Issues:**
- Booking is committed to database before email is sent
- If email fails, booking still exists (no rollback)
- Email errors not logged/handled
- No retry mechanism
- Can overload email service with concurrent requests

**Recommendation:**

Use message queue pattern (RabbitMQ/Kafka):
```gradle
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-amqp")
```

Create event:
```kotlin
// src/main/kotlin/com/grupo3/event/BookingCreatedEvent.kt
data class BookingCreatedEvent(
    val bookingId: Long,
    val userId: Long,
    val email: String,
    val hotelName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val price: Long?,
    val currency: String?,
    val createdAt: Instant
)
```

Update service:
```kotlin
@Service
class BookingService(
    private val hotelRepository: HotelRepository,
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val emailService: EmailService,
    private val rabbitTemplate: RabbitTemplate  // Inject
) {

    @Transactional
    fun createBooking(...): BookingResponseDto {
        val startTime = DateTimeUtils.parseIsoInstant(startTimeIso, "startTime")
        val endTime = DateTimeUtils.parseIsoInstant(endTimeIso, "endTime")

        require(endTime.isAfter(startTime)) { "endTime must be after startTime" }

        val hotel = hotelRepository.findById(hotelId)
            .orElseThrow { IllegalArgumentException("Hotel not found") }
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val booking = Booking(
            hotel = hotel,
            user = user,
            firstNameGuest = firstNameGuest,
            lastNameGuest = lastNameGuest,
            emailGuest = emailGuest,
            phoneNumberGuest = phoneNumberGuest,
            startTime = startTime,
            endTime = endTime,
            price = price,
            currency = currency
        )

        val savedBooking = bookingRepository.save(booking)

        // Send event (in transaction, no errors)
        val event = BookingCreatedEvent(
            bookingId = savedBooking.id!!,
            userId = user.id!!,
            email = user.email,
            hotelName = hotel.name ?: "Your hotel",
            checkInDate = BookingEmailUtils.formatInstantForEmail(startTime),
            checkOutDate = BookingEmailUtils.formatInstantForEmail(endTime),
            price = price,
            currency = currency,
            createdAt = Instant.now()
        )

        rabbitTemplate.convertAndSend("booking.created", event)

        return BookingMapper.toResponseDto(savedBooking)
    }
}

// Create separate listener
@Component
class BookingEventListener(private val emailService: EmailService) {

    @RabbitListener(queues = ["booking.created.queue"])
    fun handleBookingCreated(event: BookingCreatedEvent) {
        try {
            emailService.sendBookingConfirmationEmail(
                username = event.email.split("@")[0],
                email = event.email,
                hotelName = event.hotelName,
                guestName = "${event.firstName} ${event.lastName}",
                checkInDate = event.checkInDate,
                checkOutDate = event.checkOutDate,
                bookingId = event.bookingId.toString(),
                priceSummary = BookingEmailUtils.formatPrice(event.price, event.currency)
            )
        } catch (ex: Exception) {
            logger.error("Failed to send booking email for booking ${event.bookingId}", ex)
            // Retry logic or dead letter queue
        }
    }
}
```

**Status:** Not implemented ❌

---

### 2.5 No N+1 Query Prevention (MEDIUM)

**Location:** `src/main/kotlin/com/grupo3/service/hotel/HotelService.kt:56, 59`

**Code:**
```kotlin
fun getTopPopularHotels(): List<HotelResponseDto> =
    hotelRepository.findTop10ByIsAvailableTrueOrderByRatingDesc()
        .map { HotelMapper.toResponseDto(it) }  // Each mapping might lazy-load

fun getAllHotels(): List<HotelResponseDto> =
    hotelRepository.findAll().map { HotelMapper.toResponseDto(it) }
```

**Issue:** If `Hotel` entity has relationships (to Location, Reviews, etc.), each `.map()` might trigger additional queries.

**Recommendation:**

Add `@EntityGraph` to repository:
```kotlin
// HotelRepository.kt
import org.springframework.data.jpa.repository.EntityGraph

interface HotelRepository : JpaRepository<Hotel, String> {

    @EntityGraph(attributePaths = ["location"])  // Eager load location
    fun findTop10ByIsAvailableTrueOrderByRatingDesc(): List<Hotel>

    @EntityGraph(attributePaths = ["location"])
    override fun findAll(): List<Hotel>

    @EntityGraph(attributePaths = ["location"])
    override fun findAll(pageable: Pageable): Page<Hotel>
}
```

**Status:** Not implemented ❌

---

## 3. CODE QUALITY & MAINTAINABILITY ISSUES

### 3.1 Hardcoded Jackson Mapper (LOW)

**Location:** `src/main/kotlin/com/grupo3/service/hotel/HotelService.kt:25, 26`

**Code:**
```kotlin
@Service
class HotelService(...) {
    private val mapper = jacksonObjectMapper()  // Created every time service is instantiated

    fun searchHotelsByLocation(locationQuery: String): List<HotelResponseDto> {
        val response: LiteApiSearchResponse = mapper.readValue(json)  // Uses instance mapper
        // ...
    }
}
```

**Issues:**
- ObjectMapper created per service instance (wasteful)
- Better to inject as Spring bean
- No configuration management

**Recommendation:**
```kotlin
// src/main/kotlin/com/grupo3/config/JacksonConfig.kt
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper()
}

// HotelService.kt
@Service
class HotelService(
    private val locationService: LocationService,
    private val hotelClient: HotelClient,
    private val hotelRepository: HotelRepository,
    private val objectMapper: ObjectMapper  // Inject
) {

    fun searchHotelsByLocation(locationQuery: String): List<HotelResponseDto> {
        val response: LiteApiSearchResponse = objectMapper.readValue(json)
        // ...
    }
}
```

**Status:** Not implemented ❌

---

### 3.2 Unused/Commented Code (LOW)

**Location:** `src/main/kotlin/com/grupo3/controller/hotel/HotelController.kt:41-42`

**Code:**
```kotlin
//    @QueryMapping("semanticSearchHotel")
//    fun getHotelsByNaturalLanguage(@Argument naturalLanguage: String):List<HotelResponseDto> = hotelService.searchHotelsByNaturalLanguage(naturalLanguage)
```

**Issues:**
- Dead code clutters codebase
- Confuses developers about available functionality

**Recommendation:** Delete commented code (use git history if needed later)

**Status:** Not implemented ❌

---

### 3.3 Unnecessary Lombok Dependency (LOW)

**Location:** `build.gradle.kts:68, 71`

**Code:**
```gradle
compileOnly("org.projectlombok:lombok")
annotationProcessor("org.projectlombok:lombok")
```

**Issue:** Kotlin has data classes, which replace Lombok's @Data, @Getter, @Setter, etc. Lombok is unnecessary.

**Recommendation:** Remove Lombok dependency and verify all DTOs use Kotlin data classes.

**Status:** Not implemented ❌

---

### 3.4 Missing Logging (MEDIUM)

**Location:** Throughout codebase

**Issues:**
- No request logging (which endpoints called, by whom, when)
- No business event logging (user registered, hotel booked, etc.)
- No performance metrics
- Hard to debug issues in production

**Recommendation:**

Add Spring AOP-based request logging:
```kotlin
// src/main/kotlin/com/grupo3/config/RequestLoggingConfig.kt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
class RequestLoggingConfig {

    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        loggingFilter.setMaxPayloadLength(10000)
        loggingFilter.setIncludeHeaders(true)
        loggingFilter.setAfterMessagePrefix("REQUEST DATA : ")
        return loggingFilter
    }
}

// application.properties
logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
```

**Status:** Not implemented ❌

---

## 4. DATA VALIDATION & INPUT HANDLING

### 4.1 Insufficient Input Validation (MEDIUM)

**Location:** `src/main/kotlin/com/grupo3/service/booking/BookingService.kt:28-39`

**Code:**
```kotlin
fun createBooking(
    hotelId: String,
    userId: Long,
    firstNameGuest: String,
    lastNameGuest: String,
    emailGuest: String,
    phoneNumberGuest: String?,
    startTimeIso: String,
    endTimeIso: String,
    price: Long?,
    currency: String?
): BookingResponseDto {
    // Only validates end > start
    require(endTime.isAfter(startTime)) { "endTime must be after startTime" }
```

**Issues:**
- Guest names not validated (could be empty, SQL injection risk)
- Email only validated at DTO level
- Phone number has no format validation
- Price can be negative
- Currency not validated against ISO 4217

**Recommendation:**

Update DTO with validation:
```kotlin
// src/main/kotlin/com/grupo3/dto/booking/BookingCreateDto.kt
import jakarta.validation.constraints.*

data class BookingCreateDto(
    @field:NotBlank(message = "Hotel ID is required")
    val hotelId: String,

    @field:Positive(message = "User ID must be positive")
    val userId: Long,

    @field:NotBlank(message = "First name is required")
    @field:Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    val firstNameGuest: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 2, max = 50, message = "Last name must be 2-50 characters")
    val lastNameGuest: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val emailGuest: String,

    @field:Pattern(
        regexp = "^\\+?[1-9]\\d{1,14}$",
        message = "Phone number must be valid E.164 format"
    )
    val phoneNumberGuest: String?,

    @field:NotBlank(message = "Check-in date is required")
    val startTimeIso: String,

    @field:NotBlank(message = "Check-out date is required")
    val endTimeIso: String,

    @field:PositiveOrZero(message = "Price must be >= 0")
    val price: Long?,

    @field:Pattern(
        regexp = "^[A-Z]{3}$",
        message = "Currency must be valid ISO 4217 code (e.g., USD, EUR)"
    )
    val currency: String?
)

// Update service to use DTO
@Service
class BookingService(...) {

    @Transactional
    fun createBooking(@Valid bookingCreateDto: BookingCreateDto): BookingResponseDto {
        val startTime = DateTimeUtils.parseIsoInstant(bookingCreateDto.startTimeIso, "startTime")
        val endTime = DateTimeUtils.parseIsoInstant(bookingCreateDto.endTimeIso, "endTime")

        require(endTime.isAfter(startTime)) { "Check-out date must be after check-in date" }
        require(startTime.isAfter(Instant.now().minus(Duration.ofDays(1)))) {
            "Check-in cannot be in past"
        }

        val hotel = hotelRepository.findById(bookingCreateDto.hotelId)
            .orElseThrow { IllegalArgumentException("Hotel not found") }

        val user = userRepository.findById(bookingCreateDto.userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // ... rest of booking creation
    }
}
```

**Status:** Not implemented ❌

---

## 5. PERFORMANCE ISSUES

### 5.1 Missing Database Indexes (MEDIUM)

**Location:** `src/main/kotlin/com/grupo3/model/hotel/Hotel.kt` and `src/main/kotlin/com/grupo3/model/booking/Booking.kt`

**Issues:**
- Booking queries by `userEmail` have no index
- Hotel queries by `location` have no index
- Location queries by `code` have no index

**Recommendation:**

Add indexes to entities:
```kotlin
// Hotel.kt
import jakarta.persistence.*

@Entity
@Table(
    name = "hotels",
    indexes = [
        Index(name = "idx_hotel_location", columnList = "location"),
        Index(name = "idx_hotel_available_rating", columnList = "is_available,rating"),
        Index(name = "idx_hotel_deleted", columnList = "deleted_at")
    ]
)
data class Hotel(
    @Id val id: String = UUID.randomUUID().toString(),
    // ... fields
)

// Booking.kt
@Entity
@Table(
    name = "bookings",
    indexes = [
        Index(name = "idx_booking_user_email", columnList = "user_email"),
        Index(name = "idx_booking_user_id", columnList = "user_id"),
        Index(name = "idx_booking_hotel_id", columnList = "hotel_id"),
        Index(name = "idx_booking_dates", columnList = "start_time,end_time"),
        Index(name = "idx_booking_status", columnList = "status")
    ]
)
data class Booking(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    // ... fields
)

// Location.kt
@Entity
@Table(
    name = "locations",
    indexes = [
        Index(name = "idx_location_code", columnList = "code", unique = true),
        Index(name = "idx_location_city", columnList = "city")
    ]
)
data class Location(
    @Id val id: String = UUID.randomUUID().toString(),
    // ... fields
)
```

**Status:** Not implemented ❌

---

### 5.2 Soft Delete Not Enforced (LOW)

**Issue:** Hotel entity has `deletedAt` field but `findAll()` returns deleted hotels

**Recommendation:**

Add `@Where` annotation:
```kotlin
import org.hibernate.annotations.Where

@Entity
@Table(name = "hotels")
@Where(clause = "deleted_at IS NULL")  // Always filter
data class Hotel(
    @Id val id: String = UUID.randomUUID().toString(),
    // ... other fields
    val deletedAt: Instant? = null
)
```

**Status:** Not implemented ❌

---

## 6. ERROR HANDLING & EXCEPTION MANAGEMENT

### 6.1 Generic Exception Handler Catches Everything (HIGH)

**Location:** `src/main/kotlin/com/grupo3/exception/GlobalExceptionHandler.kt:38-49`

**Code:**
```kotlin
@ExceptionHandler(Exception::class)
fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
    val response = ErrorDetails(
        message = ex.message ?: "Unexpected error occurred",
        details = "Internal Server Error",
        localDateTime = Date(),
        code = "INTERNAL_SERVER_ERROR",
        status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
        path = request.requestURI
    )
    return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
}
```

**Issues:**
- Catches ALL exceptions (even Spring Security exceptions)
- Logs entire stack trace (security risk)
- No distinction between client errors (4xx) and server errors (5xx)
- Missing handlers for common exceptions

**Recommendation:**

```kotlin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.bind.annotation.ResponseStatus
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import java.util.Date

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // ==================== 4xx Client Errors ====================

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorDetails> {
        val violations = ex.bindingResult.fieldErrors.map {
            Violation(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value",
                rejectedValue = it.rejectedValue?.toString()
            )
        }
        val errorDetails = ErrorDetails(
            message = "Validation failed",
            details = "One or more fields have validation errors",
            localDateTime = Date(),
            code = "VALIDATION_FAILED",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI,
            violations = violations
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentials(ex: BadCredentialsException, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        logger.warn("Bad credentials attempt for path: ${request.requestURI}")
        val errorDetails = ErrorDetails(
            message = "Authentication failed",
            details = ex.message ?: "Invalid credentials",
            localDateTime = Date(),
            code = "UNAUTHORIZED",
            status = HttpStatus.UNAUTHORIZED.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUserNotFound(ex: UsernameNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        logger.warn("User not found for path: ${request.requestURI}")
        val errorDetails = ErrorDetails(
            message = "Authentication failed",
            details = "Invalid credentials",  // Don't expose user existence
            localDateTime = Date(),
            code = "UNAUTHORIZED",
            status = HttpStatus.UNAUTHORIZED.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDenied(ex: AccessDeniedException, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        logger.warn("Access denied for path: ${request.requestURI}")
        val errorDetails = ErrorDetails(
            message = "Access denied",
            details = "You do not have permission to access this resource",
            localDateTime = Date(),
            code = "FORBIDDEN",
            status = HttpStatus.FORBIDDEN.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        logger.warn("Illegal argument: ${ex.message}")
        val errorDetails = ErrorDetails(
            message = "Invalid request",
            details = ex.message ?: "Bad request",
            localDateTime = Date(),
            code = "BAD_REQUEST",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFound(ex: NoHandlerFoundException, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        val errorDetails = ErrorDetails(
            message = "Resource not found",
            details = "The requested endpoint does not exist",
            localDateTime = Date(),
            code = "NOT_FOUND",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDataIntegrityViolation(
        ex: DataIntegrityViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorDetails> {
        logger.error("Data integrity violation: ${ex.message}")
        val errorDetails = ErrorDetails(
            message = "Data conflict",
            details = "The request violates database constraints (e.g., duplicate entry)",
            localDateTime = Date(),
            code = "CONFLICT",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.CONFLICT)
    }

    // ==================== 5xx Server Errors ====================

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        logger.error("Unexpected error at ${request.requestURI}: ${ex.message}", ex)

        // Don't expose internal error details in response
        val errorDetails = ErrorDetails(
            message = "Internal server error",
            details = "An unexpected error occurred. Please contact support.",
            localDateTime = Date(),
            code = "INTERNAL_SERVER_ERROR",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
```

**Status:** Not implemented ❌

---

## 7. MISSING FEATURES & BEST PRACTICES

### 7.1 No API Documentation (MEDIUM)

**Issue:** No Swagger/OpenAPI documentation; hard for frontend developers to understand API

**Recommendation:**

Add Springdoc OpenAPI dependency:
```gradle
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
```

Create config:
```kotlin
// src/main/kotlin/com/grupo3/config/OpenApiConfig.kt
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(Info()
                .title("Ecspedia Hotel Booking API")
                .version("1.0.0")
                .description("API for hotel search, booking, and management")
                .contact(Contact()
                    .name("Ecspedia Team")
                    .url("https://ecspedia.shop")
                    .email("support@ecspedia.com"))
                .license(License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
    }
}
```

Swagger UI accessible at: `http://localhost:8080/swagger-ui.html`

**Status:** Not implemented ❌

---

### 7.2 No Health Check Endpoint (MEDIUM)

**Issue:** No way to verify application health in production

**Recommendation:**

Add dependency:
```gradle
implementation("org.springframework.boot:spring-boot-starter-actuator")
```

Update `application.properties`:
```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized
management.endpoint.health.probes.enabled=true
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
```

Access at:
- `/actuator/health` - Basic health
- `/actuator/health/liveness` - Liveness probe
- `/actuator/health/readiness` - Readiness probe

**Status:** Not implemented ❌

---

### 7.3 No Correlation IDs for Request Tracing (LOW)

**Issue:** Can't trace requests across logs in distributed system

**Recommendation:**

Create filter:
```kotlin
// src/main/kotlin/com/grupo3/config/CorrelationIdFilter.kt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import java.util.UUID

@Component
class CorrelationIdFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val correlationId = request.getHeader("X-Correlation-ID") ?: UUID.randomUUID().toString()

        MDC.put("correlationId", correlationId)
        response.addHeader("X-Correlation-ID", correlationId)

        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove("correlationId")
        }
    }
}

// Update logback.xml to include:
// <pattern>%d [%X{correlationId}] %-5p %c{1} - %m%n</pattern>
```

**Status:** Not implemented ❌

---

## 8. DATABASE & PERSISTENCE ISSUES

### 8.1 Schema Generation for Production (MEDIUM)

**Location:** `application.properties:7`

**Code:**
```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

**Issue:**
- `create-drop` only good for development
- Production needs explicit migrations

**Recommendation:**

Change to:
```properties
# application.properties
spring.jpa.hibernate.ddl-auto=validate

# Add Flyway
implementation("org.flywaydb:flyway-core:10.0.0")
implementation("org.flywaydb:flyway-database-postgresql:10.0.0")

# Create migrations in: src/main/resources/db/migration/
```

Example migration:
```sql
-- src/main/resources/db/migration/V1__Initial_schema.sql

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(10) NOT NULL UNIQUE,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    state VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_popular BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_location_code ON locations(code);
CREATE INDEX idx_location_city ON locations(city);

-- ... rest of schema
```

**Status:** Not implemented ❌

---

## 9. SUMMARY TABLE

| # | Issue | Severity | Category | Effort | Status |
|---|-------|----------|----------|--------|--------|
| 1 | Hardcoded database password | 🔴 Critical | Security | Low | ❌ |
| 2 | Weak JWT secret | 🔴 Critical | Security | Low | ❌ |
| 3 | Generic exception handling | 🔴 Critical | Error Handling | Medium | ❌ |
| 4 | Weak authentication exceptions | 🟠 High | Security | Low | ❌ |
| 5 | CORS overly permissive | 🟠 High | Security | Low | ❌ |
| 6 | No pagination on getAllHotels | 🟠 High | Performance | Medium | ❌ |
| 7 | Cache not invalidated | 🟠 High | Architecture | Low | ❌ |
| 8 | No rate limiting | 🟠 High | Security | Medium | ❌ |
| 9 | Async email in transaction | 🟡 Medium | Architecture | High | ❌ |
| 10 | Missing N+1 query prevention | 🟡 Medium | Performance | Low | ❌ |
| 11 | No database indexes | 🟡 Medium | Performance | Low | ❌ |
| 12 | Missing input validation | 🟡 Medium | Data Handling | Low | ❌ |
| 13 | Hardcoded Jackson mapper | 🟡 Medium | Code Quality | Low | ❌ |
| 14 | Missing request logging | 🟡 Medium | Observability | Low | ❌ |
| 15 | No API documentation | 🟡 Medium | Documentation | Low | ❌ |
| 16 | No health check endpoint | 🟡 Medium | Operations | Low | ❌ |
| 17 | JWT missing security claims | 🟢 Low | Security | Low | ❌ |
| 18 | Soft delete not enforced | 🟢 Low | Persistence | Low | ❌ |
| 19 | Commented-out code | 🟢 Low | Code Quality | Low | ❌ |
| 20 | Unnecessary Lombok | 🟢 Low | Dependencies | Low | ❌ |

---

## 10. RECOMMENDED IMPLEMENTATION ORDER

### Phase 1: Critical Security (1-2 days)
1. ✅ Move hardcoded secrets to environment variables
2. ✅ Fix authentication exception handling
3. ✅ Update CORS configuration
4. ✅ Strengthen JWT with security claims

### Phase 2: Data Integrity (2-3 days)
5. ✅ Add input validation to DTOs
6. ✅ Improve error handling coverage
7. ✅ Add database indexes
8. ✅ Implement cache invalidation

### Phase 3: Performance & Scalability (3-5 days)
9. ✅ Add pagination to hotel queries
10. ✅ Add @EntityGraph to prevent N+1 queries
11. ✅ Implement rate limiting
12. ✅ Switch from async email to message queue

### Phase 4: Observability & Operations (2-3 days)
13. ✅ Add API documentation (Swagger)
14. ✅ Add health check endpoints
15. ✅ Add request correlation IDs
16. ✅ Add comprehensive logging

### Phase 5: Polish & Optimization (1-2 days)
17. ✅ Remove Lombok dependency
18. ✅ Refactor hardcoded mappers
19. ✅ Remove dead code
20. ✅ Add Flyway migrations

---

## Conclusion

Your Spring Boot application has a solid foundation with modern technologies (Kotlin, Spring Boot 3, GraphQL). However, there are critical security issues and architectural decisions that need attention before production deployment.

**Most critical actions:**
1. Remove hardcoded secrets immediately
2. Implement proper authentication error handling
3. Add rate limiting to prevent abuse
4. Add pagination to prevent memory issues at scale

Once these are addressed, focus on observability and operational readiness.
