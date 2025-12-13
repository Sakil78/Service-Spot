/**
 * Validation utilities for common input fields
 * Centralized validation functions to prevent duplication across components
 *
 * @module utils/validation
 * @author ServiceSpot Team
 * @version 1.0
 */

/**
 * Validate Indian pincode format (6 digits)
 *
 * @param {string|number} pincode - Pincode to validate
 * @returns {boolean} True if valid (exactly 6 digits)
 *
 * @example
 * isValidPincode('110001') // true
 * isValidPincode('12345')  // false (only 5 digits)
 * isValidPincode('abc123') // false (contains letters)
 */
export const isValidPincode = (pincode) => {
  return /^\d{6}$/.test(String(pincode));
};

/**
 * Validate email format
 *
 * @param {string} email - Email address to validate
 * @returns {boolean} True if valid email format
 *
 * @example
 * isValidEmail('user@example.com') // true
 * isValidEmail('invalid-email')    // false
 */
export const isValidEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

/**
 * Validate Indian phone number (10 digits)
 *
 * @param {string|number} phone - Phone number to validate
 * @returns {boolean} True if valid (exactly 10 digits)
 *
 * @example
 * isValidPhone('9876543210') // true
 * isValidPhone('12345')      // false (less than 10 digits)
 */
export const isValidPhone = (phone) => {
  return /^\d{10}$/.test(String(phone));
};

/**
 * Validate geographic coordinates (latitude and longitude)
 *
 * @param {number} lat - Latitude (-90 to +90)
 * @param {number} lng - Longitude (-180 to +180)
 * @returns {boolean} True if valid coordinates
 *
 * @example
 * isValidCoordinates(28.6448, 77.2167) // true (New Delhi)
 * isValidCoordinates(100, 200)         // false (out of range)
 */
export const isValidCoordinates = (lat, lng) => {
  return (
    typeof lat === 'number' &&
    typeof lng === 'number' &&
    lat >= -90 &&
    lat <= 90 &&
    lng >= -180 &&
    lng <= 180
  );
};

/**
 * Validate password strength
 * Requirements: At least 8 characters, 1 uppercase, 1 lowercase, 1 number
 *
 * @param {string} password - Password to validate
 * @returns {boolean} True if meets requirements
 *
 * @example
 * isValidPassword('StrongPass123') // true
 * isValidPassword('weak')          // false (too short, no uppercase/number)
 */
export const isValidPassword = (password) => {
  return (
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(password)
  );
};

/**
 * Validate username (alphanumeric, 3-20 characters)
 *
 * @param {string} username - Username to validate
 * @returns {boolean} True if valid format
 *
 * @example
 * isValidUsername('user123')  // true
 * isValidUsername('ab')       // false (too short)
 * isValidUsername('user@123') // false (special characters not allowed)
 */
export const isValidUsername = (username) => {
  return /^[a-zA-Z0-9_]{3,20}$/.test(username);
};

/**
 * Validate price/amount (positive number, up to 2 decimal places)
 *
 * @param {string|number} price - Price to validate
 * @returns {boolean} True if valid price format
 *
 * @example
 * isValidPrice('99.99')  // true
 * isValidPrice('100')    // true
 * isValidPrice('-10')    // false (negative)
 * isValidPrice('10.999') // false (too many decimals)
 */
export const isValidPrice = (price) => {
  return /^\d+(\.\d{1,2})?$/.test(String(price)) && parseFloat(price) > 0;
};

/**
 * Validate URL format
 *
 * @param {string} url - URL to validate
 * @returns {boolean} True if valid URL format
 *
 * @example
 * isValidUrl('https://example.com') // true
 * isValidUrl('not-a-url')           // false
 */
export const isValidUrl = (url) => {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

// Default export with all validators
export default {
  isValidPincode,
  isValidEmail,
  isValidPhone,
  isValidCoordinates,
  isValidPassword,
  isValidUsername,
  isValidPrice,
  isValidUrl,
};

