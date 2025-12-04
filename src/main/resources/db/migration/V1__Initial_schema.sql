-- V1__Initial_schema.sql
-- Initial database schema for Ecspedia application

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- Locations table
CREATE TABLE locations (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    state VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    is_popular BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_country_city UNIQUE(country, city),
    CONSTRAINT check_city_lowercase CHECK (city = LOWER(city)),
    CONSTRAINT check_country_lowercase CHECK (country = LOWER(country))
);

CREATE INDEX idx_locations_country_city ON locations(country, city);
CREATE INDEX idx_locations_is_popular ON locations(is_popular);

-- Hotels table
CREATE TABLE hotels (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    is_available BOOLEAN NOT NULL DEFAULT true,
    rating DOUBLE PRECISION,
    review_count INTEGER,
    price_per_night DOUBLE PRECISION NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    hotel_description TEXT,
    hotel_type_id INTEGER,
    chain VARCHAR(255),
    currency VARCHAR(10),
    country VARCHAR(255),
    city VARCHAR(255),
    address VARCHAR(255),
    zip VARCHAR(20),
    main_photo VARCHAR(500),
    thumbnail VARCHAR(500),
    stars INTEGER,
    facility_ids JSONB,
    accessibility_attributes JSONB,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_hotel_city_lowercase CHECK (city = LOWER(city)),
    CONSTRAINT check_hotel_country_lowercase CHECK (country = LOWER(country))
);

CREATE INDEX idx_hotels_name ON hotels(name);
CREATE INDEX idx_hotels_location ON hotels(location);
CREATE INDEX idx_hotels_is_available ON hotels(is_available);
CREATE INDEX idx_hotels_city_country ON hotels(city, country);
CREATE INDEX idx_hotels_deleted_at ON hotels(deleted_at);

-- Bookings table
CREATE TABLE bookings (
    id VARCHAR(36) PRIMARY KEY,
    hotel_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    first_name_guest VARCHAR(80) NOT NULL,
    last_name_guest VARCHAR(80) NOT NULL,
    email_guest VARCHAR(255) NOT NULL,
    phone_number_guest VARCHAR(25),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    price BIGINT,
    currency VARCHAR(3),
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    canceled_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uk_bookings_hotel_user_slot UNIQUE(hotel_id, start_time, end_time, user_id)
);

CREATE INDEX idx_bookings_hotel_id ON bookings(hotel_id);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_start_time ON bookings(start_time);
CREATE INDEX idx_bookings_end_time ON bookings(end_time);
CREATE INDEX idx_bookings_created_at ON bookings(created_at);
