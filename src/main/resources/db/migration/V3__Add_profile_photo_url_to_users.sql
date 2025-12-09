-- V3__Add_profile_photo_url_to_users.sql
-- Add profile_photo_url column to users table

ALTER TABLE users ADD COLUMN profile_photo_url VARCHAR(500);
