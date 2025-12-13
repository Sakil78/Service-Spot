package Team.C.Service.Spot.controller;

import Team.C.Service.Spot.dto.Coordinates;
import Team.C.Service.Spot.dto.request.UpdateLocationRequest;
import Team.C.Service.Spot.dto.response.ApiResponse;
import Team.C.Service.Spot.dto.response.ServiceListingResponse;
import Team.C.Service.Spot.dto.response.UserLocationResponse;
import Team.C.Service.Spot.service.LocationService;
import Team.C.Service.Spot.service.ServiceListingService;
import Team.C.Service.Spot.service.UserService;
import Team.C.Service.Spot.model.User;
import Team.C.Service.Spot.util.DistanceCalculator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for location-based operations.
 * Provides endpoints for pincode lookup and distance-based service search.
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>GET /api/location/pincode/{pincode} - Get coordinates from pincode</li>
 *   <li>GET /api/location/nearby - Search services by location and radius</li>
 * </ul>
 *
 * @author Team C - ServiceSpot
 * @version 1.0
 * @since 2025-11-30
 */
@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService locationService;
    private final ServiceListingService serviceListingService;
    private final UserService userService;

    /**
     * Get coordinates from Indian pincode.
     * Uses free Postal Pincode API.
     *
     * @param pincode 6-digit Indian pincode
     * @return ApiResponse with Coordinates (latitude, longitude, location name)
     */
    @GetMapping("/pincode/{pincode}")
    public ResponseEntity<ApiResponse<Coordinates>> getCoordinatesFromPincode(@PathVariable Integer pincode) {
        log.info("Fetching coordinates for pincode: {}", pincode);

        try {
            Coordinates coords = locationService.getCoordinatesFromPincode(pincode);

            return ResponseEntity.ok(ApiResponse.<Coordinates>builder()
                    .success(true)
                    .message("Coordinates retrieved successfully")
                    .data(coords)
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Invalid pincode: {}", pincode, e);
            return ResponseEntity.badRequest().body(ApiResponse.<Coordinates>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error fetching coordinates for pincode: {}", pincode, e);
            return ResponseEntity.internalServerError().body(ApiResponse.<Coordinates>builder()
                    .success(false)
                    .message("Error fetching location data: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Search for services near a location.
     * Returns services within specified radius of the given pincode.
     *
     * @param pincode User's pincode
     * @param radiusKm Search radius in kilometers (default: 10 km)
     * @param category Optional service category filter
     * @return ApiResponse with list of ServiceListingResponse within radius
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<ServiceListingResponse>>> getNearbyServices(
            @RequestParam Integer pincode,
            @RequestParam(defaultValue = "10") Integer radiusKm,
            @RequestParam(required = false) String category) {

        log.info("Searching for services near pincode {} within {} km", pincode, radiusKm);

        try {
            // Get user's coordinates from pincode
            Coordinates userLocation = locationService.getCoordinatesFromPincode(pincode);

            // Search services with distance filter
            List<ServiceListingResponse> nearbyServices = serviceListingService
                    .searchServicesNearby(
                            userLocation.getLatitude(),
                            userLocation.getLongitude(),
                            radiusKm,
                            category
                    );

            String message = String.format("Found %d services within %d km of pincode %d",
                    nearbyServices.size(), radiusKm, pincode);

            return ResponseEntity.ok(ApiResponse.<List<ServiceListingResponse>>builder()
                    .success(true)
                    .message(message)
                    .data(nearbyServices)
                    .build());

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<List<ServiceListingResponse>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error searching nearby services", e);
            return ResponseEntity.internalServerError().body(ApiResponse.<List<ServiceListingResponse>>builder()
                    .success(false)
                    .message("Error searching services: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Update current user's location (for real-time tracking).
     * Allows users and providers to share their current location.
     *
     * @param request Location update request with lat/long
     * @param authentication Current authenticated user
     * @return ApiResponse confirming location update
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserLocationResponse>> updateMyLocation(
            @Valid @RequestBody UpdateLocationRequest request,
            Authentication authentication) {

        log.info("Updating location for user: {}", authentication.getName());

        try {
            // Get current user
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            // Update user's location
            user.setLatitude(request.getLatitude());
            user.setLongitude(request.getLongitude());
            userService.updateUser(user.getId(), user);

            // Build response
            UserLocationResponse response = UserLocationResponse.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .locationName(request.getLocationName())
                    .city(user.getCity())
                    .pincode(user.getPincode())
                    .lastUpdated(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(ApiResponse.<UserLocationResponse>builder()
                    .success(true)
                    .message("Location updated successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            log.error("Error updating location", e);
            return ResponseEntity.internalServerError().body(ApiResponse.<UserLocationResponse>builder()
                    .success(false)
                    .message("Error updating location: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Get location of a specific user by ID.
     * Calculates distance from current user's location if authenticated.
     * If not authenticated, returns location without distance calculation.
     *
     * @param userId Target user ID
     * @param authentication Current authenticated user (optional)
     * @return UserLocationResponse with optional distance calculation
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserLocationResponse>> getUserLocation(
            @PathVariable Long userId,
            Authentication authentication) {

        log.info("Getting location for user ID: {}", userId);
        log.debug("Authentication present: {}, Name: {}",
                 authentication != null,
                 authentication != null ? authentication.getName() : "N/A");

        try {
            // Get target user (provider)
            User targetUser = userService.getUserEntityById(userId);

            if (targetUser.getLatitude() == null || targetUser.getLongitude() == null) {
                log.warn("User {} has no coordinates set", userId);
                return ResponseEntity.badRequest().body(ApiResponse.<UserLocationResponse>builder()
                        .success(false)
                        .message("User location not available. Coordinates not set for this user.")
                        .build());
            }

            // Try to calculate distance if authenticated
            Double distance = null;
            String distanceFormatted = null;
            User currentUser = null;

            if (authentication != null && authentication.isAuthenticated()) {
                try {
                    String authName = authentication.getName();
                    log.debug("Attempting to get current user with auth name: {}", authName);

                    // Try to parse as ID first (in case token contains ID)
                    try {
                        Long currentUserId = Long.parseLong(authName);
                        currentUser = userService.getUserEntityById(currentUserId);
                        log.debug("Got current user by ID: {}", currentUser.getName());
                    } catch (NumberFormatException e) {
                        // Not a number, try as email
                        currentUser = userService.getUserByEmail(authName);
                        log.debug("Got current user by email: {}", currentUser.getName());
                    }

                    // Calculate distance if current user has coordinates
                    if (currentUser != null &&
                        currentUser.getLatitude() != null &&
                        currentUser.getLongitude() != null) {

                        distance = DistanceCalculator.calculateDistance(
                                currentUser.getLatitude(), currentUser.getLongitude(),
                                targetUser.getLatitude(), targetUser.getLongitude()
                        );
                        distanceFormatted = DistanceCalculator.formatDistance(distance);
                        log.info("✅ Calculated distance from user {} to user {}: {} km",
                                currentUser.getId(), targetUser.getId(), distance);
                    } else {
                        log.warn("Current user {} has no coordinates set",
                                currentUser != null ? currentUser.getId() : "unknown");
                    }
                } catch (Exception e) {
                    log.warn("Could not get current user for distance calculation: {}", e.getMessage());
                    // Continue without distance calculation
                }
            } else {
                log.debug("No authentication provided, skipping distance calculation");
            }

            // Build response
            UserLocationResponse response = UserLocationResponse.builder()
                    .userId(targetUser.getId())
                    .name(targetUser.getName())
                    .role(targetUser.getRole().name())
                    .latitude(targetUser.getLatitude())
                    .longitude(targetUser.getLongitude())
                    .city(targetUser.getCity())
                    .pincode(targetUser.getPincode())
                    .lastUpdated(targetUser.getUpdatedAt())
                    .distanceKm(distance)
                    .distanceFormatted(distanceFormatted)
                    .build();

            return ResponseEntity.ok(ApiResponse.<UserLocationResponse>builder()
                    .success(true)
                    .message("User location retrieved successfully")
                    .data(response)
                    .build());

        } catch (IllegalArgumentException e) {
            log.error("User not found with ID: {}", userId);
            return ResponseEntity.badRequest().body(ApiResponse.<UserLocationResponse>builder()
                    .success(false)
                    .message("User not found with ID: " + userId)
                    .build());
        } catch (Exception e) {
            log.error("Error getting user location for ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.<UserLocationResponse>builder()
                    .success(false)
                    .message("Error retrieving location: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Get location of provider for a specific booking.
     * Used by customers to track their service provider.
     *
     * @param bookingId Booking ID
     * @param authentication Current authenticated user (customer)
     * @return Provider's location with distance
     */
    @GetMapping("/booking/{bookingId}/provider")
    public ResponseEntity<ApiResponse<UserLocationResponse>> getProviderLocationForBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        log.info("Getting provider location for booking ID: {}", bookingId);

        try {
            // Get current user (customer)
            String email = authentication.getName();
            User customer = userService.getUserByEmail(email);

            // Get booking and verify ownership
            // Note: You'll need to add getBookingById method to BookingService
            // For now, this is a placeholder - implement based on your BookingService

            // Get provider from booking
            // User provider = booking.getProvider();

            // For demonstration, returning error until BookingService is integrated
            return ResponseEntity.badRequest().body(ApiResponse.<UserLocationResponse>builder()
                    .success(false)
                    .message("Booking service integration pending. Use /api/location/user/{userId} instead.")
                    .build());

        } catch (Exception e) {
            log.error("Error getting provider location for booking", e);
            return ResponseEntity.internalServerError().body(ApiResponse.<UserLocationResponse>builder()
                    .success(false)
                    .message("Error retrieving provider location: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Get my current location.
     * Returns authenticated user's stored location.
     *
     * @param authentication Current authenticated user
     * @return User's location information
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserLocationResponse>> getMyLocation(Authentication authentication) {

        log.info("Getting current user location: {}", authentication.getName());

        try {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            if (user.getLatitude() == null || user.getLongitude() == null) {
                return ResponseEntity.ok(ApiResponse.<UserLocationResponse>builder()
                        .success(false)
                        .message("Location not set. Please update your location.")
                        .build());
            }

            UserLocationResponse response = UserLocationResponse.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .latitude(user.getLatitude())
                    .longitude(user.getLongitude())
                    .city(user.getCity())
                    .pincode(user.getPincode())
                    .lastUpdated(user.getUpdatedAt())
                    .build();

            return ResponseEntity.ok(ApiResponse.<UserLocationResponse>builder()
                    .success(true)
                    .message("Location retrieved successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            log.error("Error getting current user location", e);
            return ResponseEntity.internalServerError().body(ApiResponse.<UserLocationResponse>builder()
                    .success(false)
                    .message("Error retrieving location: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Clear the pincode cache (admin only).
     * Useful for testing or forcing data refresh.
     *
     * @return ApiResponse confirming cache clearance
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<ApiResponse<Void>> clearCache() {
        log.info("Clearing location cache");

        try {
            locationService.clearCache();

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Location cache cleared successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error clearing cache", e);
            return ResponseEntity.internalServerError().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Error clearing cache: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Get cache statistics (admin only).
     * Returns information about cached pincodes.
     *
     * @return ApiResponse with cache size
     */
    @GetMapping("/cache/stats")
    public ResponseEntity<ApiResponse<Integer>> getCacheStats() {
        log.info("Fetching cache statistics");

        try {
            int cacheSize = locationService.getCacheSize();

            return ResponseEntity.ok(ApiResponse.<Integer>builder()
                    .success(true)
                    .message("Cache size: " + cacheSize + " pincodes")
                    .data(cacheSize)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching cache stats", e);
            return ResponseEntity.internalServerError().body(ApiResponse.<Integer>builder()
                    .success(false)
                    .message("Error fetching cache stats: " + e.getMessage())
                    .build());
        }
    }
}

