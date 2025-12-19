-- Migration script to add password reset functionality
-- Add reset_token and reset_token_expiry columns to users table
-- Execute this script in MySQL Workbench or any MySQL client

USE service_spot;

-- Add password reset token column
ALTER TABLE users
ADD COLUMN reset_token VARCHAR(255) NULL
COMMENT 'Hashed 6-digit password reset token'
AFTER password;

-- Add password reset token expiry column
ALTER TABLE users
ADD COLUMN reset_token_expiry DATETIME NULL
COMMENT 'Expiry timestamp for reset token (15 minutes from generation)'
AFTER reset_token;

-- Verify the changes
DESCRIBE users;

-- Check if columns were added successfully
SELECT
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'service_spot'
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME IN ('reset_token', 'reset_token_expiry');

-- Success message
SELECT 'Password reset columns added successfully!' AS status;

