package Team.C.Service.Spot.service.impl;

import Team.C.Service.Spot.dto.request.CreateServiceListingRequest;
import Team.C.Service.Spot.dto.response.ServiceListingResponse;
import Team.C.Service.Spot.mapper.ServiceListingMapper;
import Team.C.Service.Spot.model.ServiceCategory;
import Team.C.Service.Spot.model.ServiceListing;
import Team.C.Service.Spot.model.User;
import Team.C.Service.Spot.repository.ServiceCategoryRepository;
import Team.C.Service.Spot.repository.ServiceListingRepository;
import Team.C.Service.Spot.repository.BookingRepository;
import Team.C.Service.Spot.repository.SpecificAvailabilityRepository;
import Team.C.Service.Spot.service.ServiceListingService;
import Team.C.Service.Spot.service.UserService;
import Team.C.Service.Spot.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Implementation of ServiceListingService interface.
 * Handles all business logic for service listing management.
 *
 * @author Team C
 * @version 3.0
 * @since 2025-11-28
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceListingServiceImpl implements ServiceListingService {

    private final ServiceListingRepository listingRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ServiceListingMapper listingMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final SpecificAvailabilityRepository specificAvailabilityRepository;

    /**
     * Create a new service listing.
     */
    @Override
    public ServiceListingResponse createListing(CreateServiceListingRequest request, Long providerId) {
        log.info("Creating new service listing for provider ID: {}", providerId);

        // Get provider (validates existence and throws exception if not found)
        User provider = userService.getUserEntityById(providerId);

        // Validate that user is a provider
        if (!provider.isProvider()) {
            log.error("User is not a provider: {}", providerId);
            throw new IllegalArgumentException("Only providers can create service listings");
        }

        // Handle category - either use existing ID or create/find by name
        ServiceCategory category;
        if (request.getCategoryId() != null) {
            // Use provided category ID
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        log.error("Category not found with ID: {}", request.getCategoryId());
                        return new IllegalArgumentException("Category not found with ID: " + request.getCategoryId());
                    });
        } else if (request.getCategoryName() != null && !request.getCategoryName().trim().isEmpty()) {
            // Find category by name
            String categoryName = request.getCategoryName().trim();

            // List of predefined categories
            List<String> predefinedCategories = List.of(
                "Education", "Plumbing", "Electrical", "Cleaning", "Beauty",
                "IT Support", "Home Repair", "Health", "Carpentry", "Painting",
                "Appliance Repair", "Pest Control", "Moving & Delivery",
                "Gardening", "HVAC", "Automotive"
            );

            // Check if category exists in predefined list (case-insensitive)
            boolean isPredefined = predefinedCategories.stream()
                    .anyMatch(c -> c.equalsIgnoreCase(categoryName));

            if (isPredefined) {
                // Try to find existing predefined category
                category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> {
                            log.info("Creating predefined category: {}", categoryName);
                            ServiceCategory newCategory = ServiceCategory.builder()
                                    .name(categoryName)
                                    .description(categoryName + " services")
                                    .icon(getCategoryIcon(categoryName))
                                    .active(true)
                                    .displayOrder(predefinedCategories.indexOf(categoryName) + 1)
                                    .build();
                            return categoryRepository.save(newCategory);
                        });
            } else {
                // Not a predefined category - assign to "Others"
                log.info("Service '{}' does not match predefined categories. Assigning to 'Others' category", categoryName);
                category = categoryRepository.findByName("Others")
                        .orElseGet(() -> {
                            log.info("Creating 'Others' category");
                            ServiceCategory othersCategory = ServiceCategory.builder()
                                    .name("Others")
                                    .description("All other services not categorized above")
                                    .icon("OTH")
                                    .active(true)
                                    .displayOrder(17)
                                    .build();
                            return categoryRepository.save(othersCategory);
                        });
                log.info("Assigned service '{}' to 'Others' category (ID: {})", categoryName, category.getId());
            }
        } else {
            log.error("Neither categoryId nor categoryName provided");
            throw new IllegalArgumentException("Either categoryId or categoryName must be provided");
        }

        // Convert DTO to Entity
        ServiceListing listing = listingMapper.toEntity(request, provider, category);

        // Save to database
        ServiceListing savedListing = listingRepository.save(listing);
        log.info("Successfully created service listing with ID: {}", savedListing.getId());

        // Convert to Response DTO
        return listingMapper.toResponse(savedListing);
    }

    /**
     * Get service listing by ID.
     * Automatically increments view count.
     */
    @Override
    @Transactional
    public ServiceListingResponse getListingById(Long id) {
        log.info("Fetching service listing with ID: {}", id);

        ServiceListing listing = listingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Service listing not found with ID: {}", id);
                    return new IllegalArgumentException("Service listing not found with ID: " + id);
                });

        // Increment view count
        listing.incrementViewCount();
        listingRepository.save(listing);

        return listingMapper.toResponse(listing);
    }

    /**
     * Get all service listings by provider.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getListingsByProvider(Long providerId) {
        log.info("Fetching all listings for provider ID: {}", providerId);

        User provider = userService.getUserEntityById(providerId);
        List<ServiceListing> listings = listingRepository.findByProvider(provider);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active service listings by provider.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getActiveListingsByProvider(Long providerId) {
        log.info("Fetching active listings for provider ID: {}", providerId);

        User provider = userService.getUserEntityById(providerId);
        List<ServiceListing> listings = listingRepository.findByProviderAndActive(provider, true);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get service listings by category.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getListingsByCategory(Long categoryId) {
        log.info("Fetching listings for category ID: {}", categoryId);

        ServiceCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        List<ServiceListing> listings = listingRepository.findByCategoryAndActive(category, true);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get service listings by city.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getListingsByCity(String city) {
        log.info("Fetching listings in city: {}", city);

        List<ServiceListing> listings = listingRepository.findByCityAndActive(city, true);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search service listings by keyword.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> searchListings(String keyword) {
        log.info("Searching listings with keyword: {}", keyword);

        List<ServiceListing> listings = listingRepository.searchByKeyword(keyword);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active service listings.
     * Used for browse services page to show all available services.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getAllActiveListings() {
        log.info("Fetching all active listings");

        List<ServiceListing> listings = listingRepository.findByActive(true);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get featured service listings only.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getFeaturedListings() {
        log.info("Fetching featured listings");

        List<ServiceListing> listings = listingRepository.findByFeaturedAndActive(true, true);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get top-rated service listings in a city.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getTopRatedListings(String city, Double minRating) {
        log.info("Fetching top-rated listings in city: {} with min rating: {}", city, minRating);

        List<ServiceListing> listings = listingRepository.findTopRatedInCity(city, minRating);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get popular service listings in a city.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> getPopularListings(String city) {
        log.info("Fetching popular listings in city: {}", city);

        List<ServiceListing> listings = listingRepository.findPopularInCity(city);

        return listings.stream()
                .map(listingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update service listing.
     */
    @Override
    public ServiceListingResponse updateListing(Long id, CreateServiceListingRequest request, Long providerId) {
        log.info("Updating service listing ID: {} by provider ID: {}", id, providerId);

        // Find existing listing
        ServiceListing listing = listingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Service listing not found with ID: {}", id);
                    return new IllegalArgumentException("Service listing not found with ID: " + id);
                });

        // Verify ownership
        if (!listing.belongsToProvider(providerId)) {
            log.error("Provider {} does not own listing {}", providerId, id);
            throw new IllegalArgumentException("You do not have permission to update this listing");
        }

        // Get category if being updated
        if (request.getCategoryId() != null) {
            ServiceCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            listing.setCategory(category);
        }

        // Update fields
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setPriceUnit(request.getPriceUnit());
        listing.setDurationMinutes(request.getDurationMinutes());
        listing.setServiceLocation(request.getServiceLocation());
        listing.setServiceRadiusKm(request.getServiceRadiusKm());
        listing.setCity(request.getCity());
        listing.setState(request.getState());
        listing.setPincode(request.getPincode());
        listing.setImageUrl(request.getImageUrl());
        listing.setAdditionalImages(request.getAdditionalImages());

        // Save updated listing
        ServiceListing updatedListing = listingRepository.save(listing);
        log.info("Successfully updated service listing with ID: {}", id);

        return listingMapper.toResponse(updatedListing);
    }

    /**
     * Delete service listing.
     * Automatically handles cleanup of related records (availability slots, bookings) before deletion.
     * This ensures users can delete services from the UI without foreign key constraint errors.
     */
    @Override
    public void deleteListing(Long id, Long providerId) {
        log.info("🗑️ Deleting service listing ID: {} by provider ID: {}", id, providerId);

        // Find existing listing
        ServiceListing listing = listingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Service listing not found with ID: {}", id);
                    return new IllegalArgumentException("Service listing not found with ID: " + id);
                });

        // Verify ownership
        if (!listing.belongsToProvider(providerId)) {
            log.error("❌ Provider {} does not own listing {}", providerId, id);
            throw new IllegalArgumentException("You do not have permission to delete this listing");
        }

        try {
            // Step 1: Delete related availability slots (prevents foreign key constraint)
            int deletedAvailability = specificAvailabilityRepository
                .findByServiceListing(listing)
                .size();
            specificAvailabilityRepository.deleteAll(
                specificAvailabilityRepository.findByServiceListing(listing)
            );
            log.info("🧹 Cleaned up {} availability slot(s) for service ID: {}", deletedAvailability, id);

            // Step 2: Delete related bookings (prevents foreign key constraint)
            int deletedBookings = bookingRepository
                .findByServiceListing(listing)
                .size();
            bookingRepository.deleteAll(
                bookingRepository.findByServiceListing(listing)
            );
            log.info("🧹 Cleaned up {} booking(s) for service ID: {}", deletedBookings, id);

            // Step 3: Now safely delete the service listing
            listingRepository.deleteById(id);
            log.info("✅ Successfully deleted service listing with ID: {} (Cleaned: {} availability, {} bookings)",
                     id, deletedAvailability, deletedBookings);

        } catch (Exception e) {
            log.error("❌ Failed to delete service listing ID: {} - Error: {}", id, e.getMessage(), e);

            // Provide user-friendly error message
            throw new RuntimeException(
                "Failed to delete service. Please try again or contact support. Error: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Toggle listing active status.
     */
    @Override
    public ServiceListingResponse toggleListingStatus(Long id, Long providerId) {
        log.info("Toggling status for listing ID: {} by provider ID: {}", id, providerId);

        ServiceListing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service listing not found with ID: " + id));

        // Verify ownership
        if (!listing.belongsToProvider(providerId)) {
            throw new IllegalArgumentException("You do not have permission to modify this listing");
        }

        // Toggle status
        listing.setActive(!listing.getActive());
        ServiceListing updatedListing = listingRepository.save(listing);

        log.info("Successfully toggled status for listing ID: {} to {}", id, updatedListing.getActive());
        return listingMapper.toResponse(updatedListing);
    }

    /**
     * Increment view count for a listing.
     */
    @Override
    public void incrementViewCount(Long id) {
        listingRepository.findById(id).ifPresent(listing -> {
            listing.incrementViewCount();
            listingRepository.save(listing);
        });
    }

    /**
     * Update listing rating after a review.
     */
    @Override
    public void updateRating(Long id, double newRating) {
        log.info("Updating rating for listing ID: {} with new rating: {}", id, newRating);

        ServiceListing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service listing not found with ID: " + id));

        listing.updateRating(newRating);
        listingRepository.save(listing);

        log.info("Successfully updated rating for listing ID: {}", id);
    }

    /**
     * Search services near a location within specified radius.
     * Uses Haversine formula to calculate distances and filter results.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingResponse> searchServicesNearby(
            Double latitude,
            Double longitude,
            Integer radiusKm,
            String category) {

        log.info("Searching services near coordinates [{}, {}] within {} km",
                latitude, longitude, radiusKm);

        // Validate inputs
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude are required");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude. Must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude. Must be between -180 and 180");
        }
        if (radiusKm == null || radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }

        // Get all active listings (or filter by category if provided)
        List<ServiceListing> allListings;
        if (category != null && !category.isBlank()) {
            log.info("Filtering by category: {}", category);
            allListings = listingRepository.findByActive(true).stream()
                    .filter(listing -> {
                        ServiceCategory cat = listing.getCategory();
                        return cat != null && cat.getName().equalsIgnoreCase(category);
                    })
                    .collect(Collectors.toList());
        } else {
            allListings = listingRepository.findByActive(true);
        }

        log.info("Found {} active listings to check", allListings.size());

        // Filter by distance and enrich with distance information
        List<ServiceListingResponse> nearbyServices = allListings.stream()
                .filter(listing -> {
                    User provider = listing.getProvider();
                    // Check if provider has coordinates
                    if (provider.getLatitude() == null || provider.getLongitude() == null) {
                        log.debug("Skipping listing {} - provider has no coordinates", listing.getId());
                        return false;
                    }

                    // Calculate distance
                    double distance = DistanceCalculator.calculateDistance(
                            latitude, longitude,
                            provider.getLatitude(), provider.getLongitude()
                    );

                    // Check if within radius
                    boolean withinRadius = distance <= radiusKm;
                    if (withinRadius) {
                        log.debug("Listing {} is {} km away (within {} km radius)",
                                listing.getId(), String.format("%.2f", distance), radiusKm);
                    }

                    return withinRadius;
                })
                .map(listing -> {
                    ServiceListingResponse response = listingMapper.toResponse(listing);

                    // Calculate and set distance for sorting
                    User provider = listing.getProvider();
                    double distance = DistanceCalculator.calculateDistance(
                            latitude, longitude,
                            provider.getLatitude(), provider.getLongitude()
                    );

                    // Add distance to response (you may want to add a distance field to ServiceListingResponse)
                    // For now, log it
                    log.debug("Service '{}' is {} away",
                            listing.getTitle(),
                            DistanceCalculator.formatDistance(distance));

                    return response;
                })
                .sorted(Comparator.comparingDouble(response -> {
                    // Sort by distance (closest first)
                    // Re-calculate distance for sorting
                    ServiceListing listing = listingRepository.findById(response.getId()).orElse(null);
                    if (listing == null || listing.getProvider().getLatitude() == null) {
                        return Double.MAX_VALUE;
                    }
                    return DistanceCalculator.calculateDistance(
                            latitude, longitude,
                            listing.getProvider().getLatitude(),
                            listing.getProvider().getLongitude()
                    );
                }))
                .collect(Collectors.toList());

        log.info("Found {} services within {} km radius", nearbyServices.size(), radiusKm);
        return nearbyServices;
    }

    /**
     * Helper method to get appropriate icon for a category
     * @param categoryName The name of the category
     * @return Icon code for the category
     */
    private String getCategoryIcon(String categoryName) {
        return switch (categoryName.toLowerCase()) {
            case "education" -> "EDU";
            case "plumbing" -> "PLB";
            case "electrical" -> "ELE";
            case "cleaning" -> "CLN";
            case "beauty" -> "BTY";
            case "it support" -> "IT";
            case "home repair" -> "HMR";
            case "health" -> "HLT";
            case "carpentry" -> "CRP";
            case "painting" -> "PNT";
            case "appliance repair" -> "APR";
            case "pest control" -> "PST";
            case "moving & delivery" -> "MVD";
            case "gardening" -> "GRD";
            case "hvac" -> "HVC";
            case "automotive" -> "AUT";
            default -> "OTH";
        };
    }
}

