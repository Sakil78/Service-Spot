package Team.C.Service.Spot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for user location information.
 * Used to share location data between users and providers.
 *
 * @author Team C - ServiceSpot
 * @version 1.0
 * @since 2025-11-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocationResponse {

    /**
     * User ID
     */
    private Long userId;

    /**
     * User name
     */
    private String name;

    /**
     * User role (CUSTOMER or PROVIDER)
     */
    private String role;

    /**
     * Current latitude
     */
    private Double latitude;

    /**
     * Current longitude
     */
    private Double longitude;

    /**
     * Location name/address
     */
    private String locationName;

    /**
     * City
     */
    private String city;

    /**
     * Pincode
     */
    private Integer pincode;

    /**
     * When location was last updated
     */
    private LocalDateTime lastUpdated;

    /**
     * Distance from requester (in kilometers)
     * Calculated dynamically based on requester's location
     */
    private Double distanceKm;

    /**
     * Formatted distance string (e.g., "2.5 km", "850 m")
     */
    private String distanceFormatted;
}

