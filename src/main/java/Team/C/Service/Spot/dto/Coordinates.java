package Team.C.Service.Spot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for geographic coordinates.
 * Used for location-based services and distance calculations.
 *
 * @author Team C - ServiceSpot
 * @version 1.0
 * @since 2025-11-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    /**
     * Latitude coordinate (North-South position)
     * Range: -90 to +90 degrees
     */
    private Double latitude;

    /**
     * Longitude coordinate (East-West position)
     * Range: -180 to +180 degrees
     */
    private Double longitude;

    /**
     * Optional: Location name/address
     */
    private String location;

    /**
     * Constructor without location name
     */
    public Coordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

