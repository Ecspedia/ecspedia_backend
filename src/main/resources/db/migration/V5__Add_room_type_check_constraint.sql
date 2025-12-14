-- V5__Add_room_type_check_constraint.sql
-- Add CHECK constraint for room_type enum values

ALTER TABLE bookings ADD CONSTRAINT chk_room_type
    CHECK (room_type IN ('STANDARD', 'DELUXE', 'SUITE'));
