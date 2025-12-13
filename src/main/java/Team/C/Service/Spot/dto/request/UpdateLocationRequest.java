package Team.C.Service.Spot.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user's current location.
 * Used for real-time location tracking during service delivery.
 *
 * @author Team C - ServiceSpot
 * @version 1.0
 * @since 2025-11-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequest {

    /**
     * Current latitude coordinate
     * Range: -90 to +90 degrees
     */
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    /**
     * Current longitude coordinate
     * Range: -180 to +180 degrees
     */
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    /**
     * Optional address or location name
     */
    private String locationName;

    /**
     * Optional booking ID if location update is related to a booking
     */
    private Long bookingId;
}

