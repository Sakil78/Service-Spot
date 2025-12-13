import React, { useState } from 'react';
import { locationAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';
import { isValidPincode } from '../utils/validation';

/**
 * NearbyServicesSearch Component
 *
 * Search for services near a specific pincode within a radius.
 * Demonstrates the use of the locationAPI for pincode-based location tracking.
 *
 * Features:
 * - Pincode input with validation
 * - Radius selection (5, 10, 20, 50 km)
 * - Optional category filter
 * - Results display with service cards
 * - Error handling
 *
 * @component
 * @example
 * // Add to your router:
 * <Route path="/services/nearby" element={<NearbyServicesSearch />} />
 */
export default function NearbyServicesSearch() {
  const [pincode, setPincode] = useState('');
  const [radius, setRadius] = useState(10);
  const [category, setCategory] = useState('');
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchPerformed, setSearchPerformed] = useState(false);

  // Available service categories (sync with backend)
  const categories = [
    'Education',
    'Plumbing',
    'Electrical',
    'Cleaning',
    'Beauty',
    'IT Support',
    'Home Repair',
    'Health',
    'Carpentry',
    'Painting'
  ];


  /**
   * Handle search form submission
   */
  const handleSearch = async (e) => {
    e?.preventDefault();

    // Validate pincode
    if (!pincode || !isValidPincode(pincode)) {
      setError('Please enter a valid 6-digit pincode');
      return;
    }

    setLoading(true);
    setError(null);
    setSearchPerformed(true);

    try {
      console.log(`Searching for services near pincode ${pincode} within ${radius} km...`);

      const response = await locationAPI.searchNearby(
        pincode,
        radius,
        category || null
      );

      console.log('Search response:', response.data);

      if (response.data.success) {
        setServices(response.data.data || []);

        // Optional: Show success message
        if (response.data.data.length === 0) {
          setError(`No services found within ${radius} km of pincode ${pincode}. Try increasing the radius.`);
        }
      } else {
        setError(response.data.message || 'Failed to search services');
      }
    } catch (err) {
      console.error('Search error:', err);

      if (err.response?.status === 400) {
        setError(err.response.data.message || 'Invalid pincode. Please try a different pincode.');
      } else if (err.response?.status === 404) {
        setError('Pincode not found. Please check and try again.');
      } else {
        setError('Failed to search services. Please try again later.');
      }

      setServices([]);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handle pincode input change (only allow digits, max 6)
   */
  const handlePincodeChange = (e) => {
    const value = e.target.value.replace(/\D/g, ''); // Remove non-digits
    if (value.length <= 6) {
      setPincode(value);
      setError(null); // Clear error on input change
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 py-8 px-4">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            📍 Find Services Near You
          </h1>
          <p className="text-gray-600">
            Search for local service providers based on your pincode
          </p>
        </div>

        {/* Search Form */}
        <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
          <form onSubmit={handleSearch} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Pincode Input */}
              <div className="relative">
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Your Pincode *
                </label>
                <input
                  type="text"
                  placeholder="e.g., 110001"
                  value={pincode}
                  onChange={handlePincodeChange}
                  className={`w-full px-4 py-3 border-2 rounded-xl focus:outline-none focus:ring-4 transition-all ${
                    error && !isValidPincode(pincode)
                      ? 'border-red-500 focus:ring-red-500/20'
                      : 'border-purple-500/30 focus:ring-purple-500/20 focus:border-purple-500'
                  }`}
                  maxLength={6}
                  required
                />
                <span className="absolute right-3 top-11 text-gray-400 text-sm">
                  {pincode.length}/6
                </span>
              </div>

              {/* Radius Selector */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Search Radius
                </label>
                <select
                  value={radius}
                  onChange={(e) => setRadius(Number(e.target.value))}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-purple-500/20 focus:border-purple-500 appearance-none cursor-pointer bg-white"
                >
                  <option value={5}>Within 5 km</option>
                  <option value={10}>Within 10 km</option>
                  <option value={20}>Within 20 km</option>
                  <option value={50}>Within 50 km</option>
                  <option value={100}>Within 100 km</option>
                </select>
              </div>

              {/* Category Filter */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Service Category (Optional)
                </label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-purple-500/20 focus:border-purple-500 appearance-none cursor-pointer bg-white"
                >
                  <option value="">All Categories</option>
                  {categories.map((cat) => (
                    <option key={cat} value={cat}>
                      {cat}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Search Button */}
            <button
              type="submit"
              disabled={loading || !pincode || pincode.length !== 6}
              className={`w-full py-4 rounded-xl font-bold text-lg transition-all transform ${
                loading || !pincode || pincode.length !== 6
                  ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  : 'bg-gradient-to-r from-purple-600 to-pink-600 text-white hover:from-purple-700 hover:to-pink-700 hover:scale-105 shadow-lg hover:shadow-xl'
              }`}
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <LoadingSpinner />
                  Searching...
                </span>
              ) : (
                <span className="flex items-center justify-center gap-2">
                  🔍 Search Services
                </span>
              )}
            </button>
          </form>
        </div>

        {/* Error Message */}
        {error && (
          <div className="bg-red-50 border-2 border-red-200 rounded-xl p-4 mb-6">
            <div className="flex items-start gap-3">
              <span className="text-2xl">⚠️</span>
              <div className="flex-1">
                <h3 className="font-semibold text-red-800 mb-1">Error</h3>
                <p className="text-red-600">{error}</p>
              </div>
            </div>
          </div>
        )}

        {/* Results Section */}
        {searchPerformed && !loading && !error && (
          <div className="bg-white rounded-2xl shadow-xl p-6">
            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              {services.length > 0
                ? `Found ${services.length} service${services.length !== 1 ? 's' : ''} nearby`
                : 'No services found'}
            </h2>

            {services.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {services.map((service) => (
                  <div
                    key={service.id}
                    className="bg-gradient-to-br from-white to-purple-50 rounded-xl p-5 border-2 border-purple-200 hover:border-purple-400 transition-all hover:shadow-lg"
                  >
                    <h3 className="text-lg font-bold text-gray-900 mb-2">
                      {service.title}
                    </h3>
                    <p className="text-sm text-gray-600 mb-3 line-clamp-2">
                      {service.description}
                    </p>

                    <div className="space-y-2 text-sm">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-purple-600">Provider:</span>
                        <span className="text-gray-700">{service.provider?.name || 'N/A'}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-purple-600">Location:</span>
                        <span className="text-gray-700">{service.city || 'N/A'}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-purple-600">Price:</span>
                        <span className="text-gray-700 font-bold">₹{service.price}</span>
                      </div>
                      {service.category && (
                        <div className="flex items-center gap-2">
                          <span className="font-semibold text-purple-600">Category:</span>
                          <span className="text-gray-700">{service.category}</span>
                        </div>
                      )}
                    </div>

                    <button
                      onClick={() => window.location.href = `/services/${service.id}`}
                      className="mt-4 w-full bg-gradient-to-r from-purple-600 to-pink-600 text-white py-2 rounded-lg font-semibold hover:from-purple-700 hover:to-pink-700 transition-all"
                    >
                      View Details
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-12">
                <span className="text-6xl mb-4 block">🔍</span>
                <p className="text-gray-600 text-lg">
                  No services found in this area. Try:
                </p>
                <ul className="text-gray-500 mt-2 space-y-1">
                  <li>• Increasing the search radius</li>
                  <li>• Removing the category filter</li>
                  <li>• Trying a different pincode</li>
                </ul>
              </div>
            )}
          </div>
        )}

        {/* Info Box */}
        <div className="mt-8 bg-blue-50 border-2 border-blue-200 rounded-xl p-6">
          <div className="flex items-start gap-3">
            <span className="text-2xl">💡</span>
            <div>
              <h3 className="font-semibold text-blue-800 mb-2">How it works</h3>
              <ul className="text-blue-600 space-y-1 text-sm">
                <li>• Enter your 6-digit pincode</li>
                <li>• Choose how far you want to search (5-100 km)</li>
                <li>• Optionally filter by service category</li>
                <li>• Results are sorted by distance (closest first)</li>
                <li>• All distances calculated using GPS coordinates</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

