package Team.C.Service.Spot.util;

/**
 * Utility class for calculating distances between geographic coordinates.
 * Uses the Haversine formula for calculating great-circle distances.
 *
 * <p>The Haversine formula determines the great-circle distance between two points
 * on a sphere given their longitudes and latitudes. This is the shortest distance
 * over the earth's surface, giving an "as-the-crow-flies" distance.</p>
 *
 * @author Team C - ServiceSpot
 * @version 1.0
 * @since 2025-11-30
 */
public class DistanceCalculator {

    /**
     * Earth's radius in kilometers
     */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Earth's radius in miles
     */
    private static final double EARTH_RADIUS_MILES = 3958.8;

    /**
     * Private constructor to prevent instantiation
     */
    private DistanceCalculator() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }

    /**
     * Calculate the distance between two geographic coordinates using Haversine formula.
     * Returns distance in kilometers.
     *
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return calculateDistance(lat1, lon1, lat2, lon2, EARTH_RADIUS_KM);
    }

    /**
     * Calculate the distance between two geographic coordinates using Haversine formula.
     * Returns distance in miles.
     *
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @return Distance in miles
     */
    public static double calculateDistanceInMiles(double lat1, double lon1, double lat2, double lon2) {
        return calculateDistance(lat1, lon1, lat2, lon2, EARTH_RADIUS_MILES);
    }

    /**
     * Core Haversine formula implementation.
     *
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @param radius Radius of the sphere (Earth)
     * @return Distance in the same unit as radius
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2, double radius) {
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }

    /**
     * Check if a point is within a certain radius of another point.
     *
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @param radiusKm Radius in kilometers
     * @return true if second point is within radius of first point
     */
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= radiusKm;
    }

    /**
     * Format distance for display with appropriate unit.
     *
     * @param distanceKm Distance in kilometers
     * @return Formatted string (e.g., "2.5 km" or "850 m")
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            int meters = (int) (distanceKm * 1000);
            return meters + " m";
        } else {
            return String.format("%.1f km", distanceKm);
        }
    }
}

