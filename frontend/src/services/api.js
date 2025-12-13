import axios from 'axios';

/**
 * Replace this with your backend URL when ready (e.g. 'http://localhost:8080/api')
 * or wire to env var later if you prefer.
 */
const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  // optional: avoid hanging requests in dev
  timeout: 10000,
});

// Request interceptor: attach token if present
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: handle 401 globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // If unauthorized, clear session and navigate to login
    if (error.response?.status === 401) {
      try {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        // full reload to landing/login — acceptable during early dev
        window.location.href = '/login';
      } catch (e) {
        /* ignore */
      }
    }
    return Promise.reject(error);
  }
);

/* ---------------------------
   Admin APIs
   --------------------------- */
export const adminAPI = {
  signup: (data) => api.post('/auth/register/admin', data),
  login: (data) => api.post('/auth/login', data),
  getStatistics: () => api.get('/admin/statistics'),
  getAllUsers: (role) => api.get('/admin/users', { params: { role } }),
  getPendingVerifications: () => api.get('/admin/users/pending-verification'),
  verifyProvider: (userId) => api.post(`/admin/users/${userId}/verify`),
  suspendUser: (userId) => api.post(`/admin/users/${userId}/suspend`),
  reactivateUser: (userId) => api.post(`/admin/users/${userId}/reactivate`),
  deleteUser: (userId) => api.delete(`/admin/users/${userId}`),
  getAllBookings: () => api.get('/admin/bookings'),
  getRecentUsers: (days, limit) => api.get('/admin/users/recent', { params: { days, limit } }),
};

/* ---------------------------
   Customer APIs
   --------------------------- */
export const customerAPI = {
  signup: (data) => api.post('/auth/register/customer', data),
  login: (data) => api.post('/auth/login', data),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
  getById: (id) => api.get(`/users/${id}`),
  getStatistics: (customerId) => api.get(`/customers/${customerId}/statistics`),
  getRecentBookings: (customerId, limit) => api.get(`/customers/${customerId}/bookings/recent`, { params: { limit } }),
  getUpcomingBookings: (customerId) => api.get(`/customers/${customerId}/bookings/upcoming`),
};

/* ---------------------------
   Provider APIs
   --------------------------- */
export const providerAPI = {
  signup: (data) => api.post('/auth/register/provider', data),
  login: (data) => api.post('/auth/login', data),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
  getAll: () => api.get('/users/providers/search', { params: { keyword: '' } }),
  getById: (id) => api.get(`/users/${id}`),
  searchByCity: (city) => api.get(`/users/providers/city/${city}`),
  getAvailableCities: () => api.get('/users/providers/locations/cities'),
  getAvailableServiceTypes: () => api.get('/users/providers/service-types'),
};

/* ---------------------------
   Service APIs
   --------------------------- */
export const serviceAPI = {
  search: (params) => api.get('/services', { params }),
  getById: (id) => api.get(`/services/${id}`),
  getByProvider: (providerId) => api.get(`/services/provider/${providerId}`),
  create: (data, providerId) => api.post('/services', data, { params: { providerId } }),
  update: (id, data, providerId) => api.put(`/services/${id}`, data, { params: { providerId } }),
  delete: (id, providerId) => api.delete(`/services/${id}`, { params: { providerId } }),
};

/* ---------------------------
   Booking APIs
   --------------------------- */
export const bookingAPI = {
  create: (data) => api.post('/bookings', data),
  getByUser: (userId, role) => api.get('/bookings', { params: { userId, role } }),
  getById: (id) => api.get(`/bookings/${id}`),
  updateStatus: (id, status, additionalData = {}) => {
    const payload = {
      status,
      providerNotes: additionalData.providerNotes || null,
      cancellationReason: additionalData.cancellationReason || null,
      cancelledBy: additionalData.cancelledBy || null
    };
    console.log('📤 API updateStatus called:', { id, payload });
    return api.patch(`/bookings/${id}`, payload);
  },
  cancel: (id) => api.delete(`/bookings/${id}`),
};

/* ---------------------------
   Review APIs
   --------------------------- */
export const reviewAPI = {
  create: (data) => api.post('/reviews', data),
  getByProvider: (providerId) => api.get(`/reviews/provider/${providerId}`),
  getByBooking: (bookingId) => api.get(`/reviews/booking/${bookingId}`),
};

/* ---------------------------
   Availability APIs
   --------------------------- */
export const availabilityAPI = {
  create: (data) => api.post('/availability', data),
  getByProvider: (providerId, date) =>
    api.get(`/availability/provider/${providerId}`, { params: { date } }),
  // Get availability for entire month (for calendar)
  getMonth: ({ providerId, serviceId, year, month }) =>
    api.get(`/availability/provider/${providerId}`, {
      params: { year, month, serviceId }
    }),
  // Get available slots for a specific date
  getSlots: ({ providerId, serviceId, date }) =>
    api.get(`/availability/provider/${providerId}`, {
      params: { date, serviceId }
    }),
  update: (id, data) => api.put(`/availability/${id}`, data),
  delete: (id) => api.delete(`/availability/${id}`),
};

/* ---------------------------
   Specific Availability APIs (Date-based)
   --------------------------- */
export const specificAvailabilityAPI = {
  // Create specific availability
  create: (data) => api.post('/specific-availability', data),

  // Get by ID
  getById: (id) => api.get(`/specific-availability/${id}`),

  // Get provider's availability
  getByProvider: (providerId, futureOnly = true) =>
    api.get(`/specific-availability/provider/${providerId}`, { params: { futureOnly } }),

  // Get service's availability
  getByService: (serviceListingId, futureOnly = true) =>
    api.get(`/specific-availability/service/${serviceListingId}`, { params: { futureOnly } }),

  // Get available dates in a range
  getAvailableDates: (providerId, startDate, endDate) =>
    api.get(`/specific-availability/provider/${providerId}/dates`, {
      params: { startDate, endDate }
    }),

  // Get available dates for a service
  getAvailableDatesForService: (serviceListingId, startDate, endDate) =>
    api.get(`/specific-availability/service/${serviceListingId}/dates`, {
      params: { startDate, endDate }
    }),

  // Get time slots for a specific date
  getTimeSlots: (providerId, date) =>
    api.get(`/specific-availability/provider/${providerId}/slots`, { params: { date } }),

  // Get time slots for a specific date and service
  getTimeSlotsForService: (serviceListingId, date) =>
    api.get(`/specific-availability/service/${serviceListingId}/slots`, { params: { date } }),

  // Update availability
  update: (id, data) => api.put(`/specific-availability/${id}`, data),

  // Delete availability
  delete: (id) => api.delete(`/specific-availability/${id}`),

  // Mark as unavailable
  markUnavailable: (id) => api.patch(`/specific-availability/${id}/unavailable`),

  // Mark as available
  markAvailable: (id) => api.patch(`/specific-availability/${id}/available`),
};

/* ---------------------------
   Location-Based Service Search APIs
   --------------------------- */
export const locationAPI = {
  /**
   * Get geographic coordinates (latitude, longitude) from an Indian pincode.
   * Uses Indian Postal Pincode API via backend.
   *
   * @param {number|string} pincode - 6-digit Indian pincode
   * @returns {Promise} Response with { latitude, longitude, location }
   *
   * @example
   * const coords = await locationAPI.getCoordinates(110001);
   * // Returns: { latitude: 28.6448, longitude: 77.2167, location: "New Delhi..." }
   */
  getCoordinates: (pincode) => api.get(`/location/pincode/${pincode}`),

  /**
   * Search for services near a specific pincode within a given radius.
   * Results are sorted by distance (closest first).
   *
   * @param {number|string} pincode - User's pincode
   * @param {number} radiusKm - Search radius in kilometers (default: 10)
   * @param {string|null} category - Optional service category filter (e.g., "Plumbing")
   * @returns {Promise} List of services within the specified radius
   *
   * @example
   * // Find all services within 10 km
   * const services = await locationAPI.searchNearby(110001, 10);
   *
   * @example
   * // Find plumbing services within 20 km
   * const plumbingServices = await locationAPI.searchNearby(110001, 20, 'Plumbing');
   */
  searchNearby: (pincode, radiusKm = 10, category = null) => {
    const params = new URLSearchParams({
      pincode: String(pincode),
      radiusKm: String(radiusKm)
    });
    if (category) {
      params.append('category', category);
    }
    return api.get(`/location/nearby?${params.toString()}`);
  },

  /**
   * Update my current location (real-time tracking).
   * Allows users and providers to share their current GPS coordinates.
   *
   * @param {Object} locationData - Location update data
   * @param {number} locationData.latitude - Current latitude
   * @param {number} locationData.longitude - Current longitude
   * @param {string} [locationData.locationName] - Optional location name
   * @param {number} [locationData.bookingId] - Optional related booking ID
   * @returns {Promise} Updated location response
   *
   * @example
   * const updated = await locationAPI.updateMyLocation({
   *   latitude: 28.6448,
   *   longitude: 77.2167,
   *   locationName: "New Delhi Railway Station"
   * });
   */
  updateMyLocation: (locationData) => api.put('/location/update', locationData),

  /**
   * Get location of a specific user by ID.
   * Calculates distance from my current location.
   *
   * @param {number} userId - Target user ID
   * @returns {Promise} User location with distance calculation
   *
   * @example
   * const providerLocation = await locationAPI.getUserLocation(123);
   * // Returns: { latitude, longitude, distanceKm, distanceFormatted, ... }
   */
  getUserLocation: (userId) => api.get(`/location/user/${userId}`),

  /**
   * Get provider's location for a specific booking.
   * Customers can track their service provider's location.
   *
   * @param {number} bookingId - Booking ID
   * @returns {Promise} Provider location with distance
   *
   * @example
   * const providerLocation = await locationAPI.getProviderLocationForBooking(456);
   */
  getProviderLocationForBooking: (bookingId) =>
    api.get(`/location/booking/${bookingId}/provider`),

  /**
   * Get my current location.
   * Returns authenticated user's stored location coordinates.
   *
   * @returns {Promise} My location information
   *
   * @example
   * const myLocation = await locationAPI.getMyLocation();
   * console.log(`I'm at ${myLocation.data.latitude}, ${myLocation.data.longitude}`);
   */
  getMyLocation: () => api.get('/location/me'),

  /**
   * Clear the pincode geocoding cache (Admin only).
   * Useful for forcing fresh data or troubleshooting.
   *
   * @returns {Promise} Success/failure response
   */
  clearCache: () => api.post('/location/cache/clear'),

  /**
   * Get cache statistics - number of cached pincodes (Admin only).
   * Useful for monitoring performance.
   *
   * @returns {Promise} Cache size and statistics
   */
  getCacheStats: () => api.get('/location/cache/stats'),
};

export default api;
