-- V4__Add_room_type_to_bookings.sql
-- Add room_type column to bookings table

ALTER TABLE bookings ADD COLUMN room_type VARCHAR(20) NOT NULL DEFAULT 'STANDARD';
