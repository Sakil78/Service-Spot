import React, { useState, useEffect } from 'react';
import { locationAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

/**
 * RealTimeLocationTracker Component
 *
 * Enables real-time location tracking between users and providers.
 * Features:
 * - Get current GPS location from browser
 * - Share location with backend
 * - Track other user's location
 * - Calculate and display distance
 * - Auto-refresh location periodically
 *
 * @component
 * @example
 * // Track a provider's location
 * <RealTimeLocationTracker
 *   targetUserId={123}
 *   targetUserName="John's Plumbing"
 *   autoRefresh={true}
 * />
 */
export default function RealTimeLocationTracker({
  targetUserId = null,
  targetUserName = "User",
  autoRefresh = true,
  refreshInterval = 30000 // 30 seconds
}) {
  const [myLocation, setMyLocation] = useState(null);
  const [targetLocation, setTargetLocation] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [lastUpdated, setLastUpdated] = useState(null);
  const [sharingLocation, setSharingLocation] = useState(false);

  /**
   * Get current GPS location from browser
   */
  const getCurrentPosition = () => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by your browser'));
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: position.coords.accuracy
          });
        },
        (error) => {
          reject(error);
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        }
      );
    });
  };

  /**
   * Share my current location with backend
   */
  const shareMyLocation = async () => {
    try {
      setSharingLocation(true);
      setError(null);

      // Get GPS coordinates
      const position = await getCurrentPosition();

      // Send to backend
      const response = await locationAPI.updateMyLocation({
        latitude: position.latitude,
        longitude: position.longitude,
        locationName: `GPS: ${position.accuracy.toFixed(0)}m accuracy`
      });

      if (response.data.success) {
        setMyLocation(response.data.data);
        setLastUpdated(new Date());
        console.log('Location shared successfully:', response.data.data);
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      console.error('Error sharing location:', err);
      if (err.code === 1) {
        setError('Location permission denied. Please enable location access in your browser.');
      } else if (err.code === 2) {
        setError('Location unavailable. Please check your device settings.');
      } else if (err.code === 3) {
        setError('Location request timeout. Please try again.');
      } else {
        setError('Failed to share location: ' + (err.response?.data?.message || err.message));
      }
    } finally {
      setSharingLocation(false);
    }
  };

  /**
   * Get target user's location
   */
  const getTargetLocation = async () => {
    if (!targetUserId) return;

    try {
      setLoading(true);
      setError(null);

      const response = await locationAPI.getUserLocation(targetUserId);

      if (response.data.success) {
        setTargetLocation(response.data.data);
        setLastUpdated(new Date());
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      console.error('Error getting target location:', err);
      setError('Failed to get location: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  /**
   * Get my saved location from backend
   */
  const getMyLocation = async () => {
    try {
      const response = await locationAPI.getMyLocation();
      if (response.data.success) {
        setMyLocation(response.data.data);
      }
    } catch (err) {
      console.error('Error getting my location:', err);
    }
  };

  /**
   * Initialize - get locations on mount
   */
  useEffect(() => {
    getMyLocation();
    if (targetUserId) {
      getTargetLocation();
    }
  }, [targetUserId]);

  /**
   * Auto-refresh target location
   */
  useEffect(() => {
    if (!autoRefresh || !targetUserId) return;

    const interval = setInterval(() => {
      getTargetLocation();
    }, refreshInterval);

    return () => clearInterval(interval);
  }, [autoRefresh, targetUserId, refreshInterval]);

  /**
   * Open location in Google Maps
   */
  const openInMaps = (lat, lng) => {
    window.open(`https://www.google.com/maps?q=${lat},${lng}`, '_blank');
  };

  return (
    <div className="bg-white rounded-xl shadow-lg p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">
          📍 Location Tracking
        </h2>
        {lastUpdated && (
          <span className="text-sm text-gray-500">
            Updated: {lastUpdated.toLocaleTimeString()}
          </span>
        )}
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border-2 border-red-200 rounded-lg p-4">
          <div className="flex items-start gap-3">
            <span className="text-2xl">⚠️</span>
            <div className="flex-1">
              <h3 className="font-semibold text-red-800">Error</h3>
              <p className="text-red-600 text-sm">{error}</p>
            </div>
          </div>
        </div>
      )}

      {/* My Location Section */}
      <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-4">
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-lg font-semibold text-blue-900">
            📱 My Location
          </h3>
          <button
            onClick={shareMyLocation}
            disabled={sharingLocation}
            className={`px-4 py-2 rounded-lg font-semibold text-sm transition-all ${
              sharingLocation
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                : 'bg-blue-600 text-white hover:bg-blue-700'
            }`}
          >
            {sharingLocation ? (
              <span className="flex items-center gap-2">
                <LoadingSpinner />
                Sharing...
              </span>
            ) : (
              '🔄 Share My Location'
            )}
          </button>
        </div>

        {myLocation ? (
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-blue-700 font-medium">Coordinates:</span>
              <span className="text-blue-900 font-mono">
                {myLocation.latitude?.toFixed(6)}, {myLocation.longitude?.toFixed(6)}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-blue-700 font-medium">City:</span>
              <span className="text-blue-900">{myLocation.city || 'N/A'}</span>
            </div>
            <button
              onClick={() => openInMaps(myLocation.latitude, myLocation.longitude)}
              className="w-full mt-2 bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 transition-all"
            >
              📍 Open in Google Maps
            </button>
          </div>
        ) : (
          <div className="text-center text-blue-600 py-4">
            <p>No location shared yet.</p>
            <p className="text-sm">Click "Share My Location" to enable tracking.</p>
          </div>
        )}
      </div>

      {/* Target User Location Section */}
      {targetUserId && (
        <div className="bg-gradient-to-br from-purple-50 to-purple-100 rounded-lg p-4">
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-lg font-semibold text-purple-900">
              🎯 {targetUserName}'s Location
            </h3>
            <button
              onClick={getTargetLocation}
              disabled={loading}
              className={`px-4 py-2 rounded-lg font-semibold text-sm transition-all ${
                loading
                  ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  : 'bg-purple-600 text-white hover:bg-purple-700'
              }`}
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <LoadingSpinner />
                  Loading...
                </span>
              ) : (
                '🔄 Refresh'
              )}
            </button>
          </div>

          {targetLocation ? (
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-purple-700 font-medium">Coordinates:</span>
                <span className="text-purple-900 font-mono">
                  {targetLocation.latitude?.toFixed(6)}, {targetLocation.longitude?.toFixed(6)}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-purple-700 font-medium">City:</span>
                <span className="text-purple-900">{targetLocation.city || 'N/A'}</span>
              </div>
              {targetLocation.distanceFormatted && (
                <div className="flex justify-between">
                  <span className="text-purple-700 font-medium">Distance:</span>
                  <span className="text-purple-900 font-bold">
                    {targetLocation.distanceFormatted}
                  </span>
                </div>
              )}
              <button
                onClick={() => openInMaps(targetLocation.latitude, targetLocation.longitude)}
                className="w-full mt-2 bg-purple-600 text-white py-2 rounded-lg font-semibold hover:bg-purple-700 transition-all"
              >
                📍 Open in Google Maps
              </button>
            </div>
          ) : (
            <div className="text-center text-purple-600 py-4">
              {loading ? (
                <LoadingSpinner />
              ) : (
                <>
                  <p>Location not available</p>
                  <p className="text-sm">User hasn't shared their location yet.</p>
                </>
              )}
            </div>
          )}
        </div>
      )}

      {/* Map View Link (Both locations) */}
      {myLocation && targetLocation && (
        <button
          onClick={() => {
            const url = `https://www.google.com/maps/dir/${myLocation.latitude},${myLocation.longitude}/${targetLocation.latitude},${targetLocation.longitude}`;
            window.open(url, '_blank');
          }}
          className="w-full bg-gradient-to-r from-green-600 to-blue-600 text-white py-3 rounded-lg font-bold hover:from-green-700 hover:to-blue-700 transition-all"
        >
          🗺️ Get Directions (Google Maps)
        </button>
      )}

      {/* Info */}
      <div className="bg-yellow-50 border-2 border-yellow-200 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <span className="text-2xl">💡</span>
          <div className="text-sm text-yellow-800">
            <p className="font-semibold mb-1">Location Tracking Tips:</p>
            <ul className="space-y-1 list-disc list-inside">
              <li>Enable GPS/Location services on your device</li>
              <li>Grant location permission when prompted</li>
              <li>Location updates every {refreshInterval / 1000} seconds</li>
              <li>Distance is calculated as the crow flies</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

