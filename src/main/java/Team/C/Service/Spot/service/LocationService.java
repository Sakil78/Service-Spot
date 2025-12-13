package Team.C.Service.Spot.service;

import Team.C.Service.Spot.dto.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service for location-based operations using Nominatim (OpenStreetMap) API.
 *
 * This service provides REAL GPS coordinates from OpenStreetMap,
 * not fake regional mappings.
 *
 * <p>API: Nominatim (OpenStreetMap Geocoding)</p>
 * <p>Documentation: https://nominatim.org/release-docs/latest/api/Search/</p>
 * <p>Features:</p>
 * <ul>
 *   <li>Real GPS coordinates for Indian pincodes</li>
 *   <li>Free service (no API key required)</li>
 *   <li>Caching for performance</li>
 *   <li>Rate limit compliant (1 req/sec)</li>
 * </ul>
 *
 * @author Team C - ServiceSpot
 * @version 2.0 - Using Real Geocoding
 * @since 2025-12-01
 */
@Service
@Slf4j
public class LocationService {

    private final RestTemplate restTemplate;

    /**
     * In-memory cache for pincode coordinates
     * Key: Pincode, Value: Coordinates
     */
    private final Map<Integer, Coordinates> pincodeCache = new HashMap<>();

    /**
     * Nominatim API (OpenStreetMap) - Primary geocoding service
     */
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";

    /**
     * Alternative geocoding service (fallback when Nominatim is blocked/fails)
     */
    private static final String GEOCODE_XYZ_API_URL = "https://geocode.xyz";

    /**
     * User-Agent required by Nominatim API (their usage policy)
     * Must be descriptive and include contact information
     */
    private static final String USER_AGENT = "ServiceSpot/1.0 (https://servicespot.com; contact@servicespot.com)";

    /**
     * Referer header for Nominatim (optional but recommended)
     */
    private static final String REFERER = "https://servicespot.com";

    /**
     * Rate limiter: Last API call timestamp
     * Nominatim requires at least 1 second between requests
     */
    private long lastApiCallTime = 0;

    /**
     * Minimum delay between API calls (milliseconds)
     * Nominatim policy: max 1 request per second
     */
    private static final long MIN_DELAY_MS = 1000;

    @Autowired
    public LocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Enforce rate limit: Wait if needed before making API call.
     * Nominatim requires at least 1 second between requests.
     */
    private synchronized void enforceRateLimit() {
        long now = System.currentTimeMillis();
        long timeSinceLastCall = now - lastApiCallTime;

        if (timeSinceLastCall < MIN_DELAY_MS) {
            long waitTime = MIN_DELAY_MS - timeSinceLastCall;
            try {
                log.debug("Rate limiting: waiting {} ms before API call", waitTime);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Rate limiter interrupted");
            }
        }

        lastApiCallTime = System.currentTimeMillis();
    }

    /**
     * Fetch REAL GPS coordinates for an Indian pincode.
     * Tries Nominatim first, falls back to alternative service if blocked/failed.
     *
     * @param pincode 6-digit Indian pincode
     * @return Coordinates with real latitude, longitude, and location name
     * @throws IllegalArgumentException if pincode is invalid
     * @throws IllegalStateException if all geocoding services fail
     */
    public Coordinates getCoordinatesFromPincode(Integer pincode) {
        // Validate pincode
        if (pincode == null || pincode < 100000 || pincode > 999999) {
            log.error("Invalid pincode format: {}", pincode);
            throw new IllegalArgumentException("Invalid pincode format. Must be 6 digits.");
        }

        // Check cache first
        if (pincodeCache.containsKey(pincode)) {
            log.info("✅ Cache hit for pincode: {}", pincode);
            return pincodeCache.get(pincode);
        }

        // Try Nominatim first
        try {
            log.info("🔍 Attempting Nominatim (OpenStreetMap) for pincode: {}", pincode);
            Coordinates coords = fetchFromNominatim(pincode);
            pincodeCache.put(pincode, coords);
            return coords;
        } catch (Exception e) {
            log.warn("⚠️ Nominatim failed for pincode {}: {}", pincode, e.getMessage());
            log.info("🔄 Trying alternative geocoding service...");
        }

        // Fallback to alternative service
        try {
            Coordinates coords = fetchFromGeocodeXyz(pincode);
            pincodeCache.put(pincode, coords);
            return coords;
        } catch (Exception e) {
            log.error("❌ All geocoding services failed for pincode {}", pincode);
            throw new IllegalStateException(
                "Unable to geocode pincode " + pincode +
                ". All geocoding services are unavailable or blocked."
            );
        }
    }

    /**
     * Fetch coordinates from Nominatim (OpenStreetMap).
     * Primary geocoding service.
     */
    private Coordinates fetchFromNominatim(Integer pincode) {

        try {
            // Enforce rate limit (1 request per second)
            enforceRateLimit();

            // Build Nominatim API URL
            String url = NOMINATIM_API_URL +
                        "?postalcode=" + pincode +
                        "&country=India" +
                        "&format=json" +
                        "&limit=1";  // Only need first result

            log.debug("Nominatim API URL: {}", url);

            // Set required headers (Nominatim policy compliance)
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            headers.set("Referer", REFERER);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Call Nominatim API
            ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                List.class
            );

            log.debug("API Response Status: {}", response.getStatusCode());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> responseBody = (List<Map<String, Object>>) response.getBody();

            if (responseBody == null || responseBody.isEmpty()) {
                log.error("❌ Nominatim returned no results for pincode: {}", pincode);
                throw new IllegalStateException(
                    "No geocoding data found for pincode " + pincode +
                    ". The pincode may not exist in OpenStreetMap database."
                );
            }

            // Get first result (most relevant)
            Map<String, Object> location = responseBody.get(0);
            log.debug("Nominatim location data: {}", location);

            // Extract REAL coordinates (Nominatim returns them as strings)
            String latStr = (String) location.get("lat");
            String lonStr = (String) location.get("lon");
            String displayName = (String) location.get("display_name");

            if (latStr == null || lonStr == null) {
                log.error("❌ Nominatim returned null coordinates for pincode: {}", pincode);
                throw new IllegalStateException(
                    "Incomplete geocoding data for pincode " + pincode
                );
            }

            Double latitude = Double.parseDouble(latStr);
            Double longitude = Double.parseDouble(lonStr);

            Coordinates coordinates = new Coordinates(
                latitude,
                longitude,
                displayName != null ? displayName : "Location in India"
            );

            // Cache the result
            pincodeCache.put(pincode, coordinates);

            log.info("✅ Nominatim: Successfully fetched coordinates for pincode {}: lat={}, lon={}",
                     pincode, latitude, longitude);

            return coordinates;

        } catch (Exception e) {
            log.warn("⚠️ Nominatim failed for pincode {}: {} ({})",
                    pincode, e.getMessage(), e.getClass().getSimpleName());
            throw new IllegalStateException("Nominatim unavailable", e);
        }
    }

    /**
     * Fetch coordinates from Geocode.xyz (alternative/fallback service).
     * Free service, no API key required.
     */
    private Coordinates fetchFromGeocodeXyz(Integer pincode) {
        log.info("🔄 Trying Geocode.xyz for pincode: {}", pincode);

        // Enforce rate limit
        enforceRateLimit();

        try {
            // Build Geocode.xyz API URL
            String url = GEOCODE_XYZ_API_URL + "/" + pincode + "?json=1&region=IN";

            log.debug("Geocode.xyz API URL: {}", url);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Call API
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

            if (responseBody == null || responseBody.isEmpty()) {
                throw new IllegalStateException("Empty response from Geocode.xyz");
            }

            // Extract coordinates (different format than Nominatim)
            Object latObj = responseBody.get("latt");
            Object lonObj = responseBody.get("longt");

            if (latObj == null || lonObj == null) {
                throw new IllegalStateException("No coordinates in Geocode.xyz response");
            }

            Double latitude = Double.parseDouble(latObj.toString());
            Double longitude = Double.parseDouble(lonObj.toString());

            String location = responseBody.get("standard") != null ?
                            responseBody.get("standard").toString() :
                            "India, " + pincode;

            Coordinates coordinates = new Coordinates(latitude, longitude, location);

            log.info("✅ Geocode.xyz: Successfully fetched coordinates for pincode {}: lat={}, lon={}",
                     pincode, latitude, longitude);

            return coordinates;

        } catch (Exception e) {
            log.error("❌ Geocode.xyz failed for pincode {}: {} ({})",
                     pincode, e.getMessage(), e.getClass().getSimpleName());
            throw new IllegalStateException("Geocode.xyz unavailable", e);
        }
    }

    /**
     * Calculate distance between two coordinates using Haversine formula.
     *
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Clear the pincode cache.
     * Useful for testing or refreshing data.
     */
    public void clearCache() {
        pincodeCache.clear();
        log.info("Pincode cache cleared");
    }

    /**
     * Get cache size for monitoring.
     *
     * @return Number of cached pincodes
     */
    public int getCacheSize() {
        return pincodeCache.size();
    }
}

