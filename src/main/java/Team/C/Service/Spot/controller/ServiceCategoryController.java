package Team.C.Service.Spot.controller;

import Team.C.Service.Spot.dto.response.ApiResponse;
import Team.C.Service.Spot.dto.response.ServiceCategoryResponse;
import Team.C.Service.Spot.model.ServiceCategory;
import Team.C.Service.Spot.model.ServiceListing;
import Team.C.Service.Spot.repository.ServiceCategoryRepository;
import Team.C.Service.Spot.repository.ServiceListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Service Category operations.
 * Handles retrieval and management of service categories.
 *
 * @author Team C
 * @version 3.0
 * @since 2025-11-29
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ServiceCategoryController {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceListingRepository serviceListingRepository;

    /**
     * Get all service categories.
     *
     * @return list of all categories
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        List<ServiceCategoryResponse> response = categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", response));
    }

    /**
     * Get only categories that have active service listings.
     * This is used for the landing page to show only categories with available services.
     * Includes "Others" category if it has services.
     *
     * @return list of categories with active services
     */
    @GetMapping("/with-services")
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> getCategoriesWithServices() {
        try {
            // Get all active service listings
            List<ServiceListing> allActiveServices = serviceListingRepository.findByActive(true);

            // Get unique category IDs from active services
            java.util.Set<Long> categoryIdsWithServices = allActiveServices.stream()
                    .map(service -> service.getCategory() != null ? service.getCategory().getId() : null)
                    .filter(id -> id != null)
                    .collect(java.util.stream.Collectors.toSet());

            // Get all categories
            List<ServiceCategory> allCategories = serviceCategoryRepository.findAll();

            // Filter to only categories that have services
            List<ServiceCategoryResponse> categoriesWithServices = allCategories.stream()
                    .filter(category -> categoryIdsWithServices.contains(category.getId()))
                    .sorted((c1, c2) -> {
                        Integer order1 = c1.getDisplayOrder() != null ? c1.getDisplayOrder() : 999;
                        Integer order2 = c2.getDisplayOrder() != null ? c2.getDisplayOrder() : 999;
                        return order1.compareTo(order2);
                    })
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(
                "Categories with services retrieved successfully",
                categoriesWithServices
            ));
        } catch (Exception e) {
            // Log the error for debugging
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.success(
                "Categories retrieved with fallback",
                new java.util.ArrayList<>()
            ));
        }
    }

    /**
     * Get category by ID.
     *
     * @param id category ID
     * @return category details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> getCategoryById(@PathVariable Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", mapToResponse(category)));
    }

    /**
     * Get category by name.
     *
     * @param name category name
     * @return category details
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> getCategoryByName(@PathVariable String name) {
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        ServiceCategory category = categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Category not found with name: " + name));
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", mapToResponse(category)));
    }

    /**
     * Search categories by keyword.
     *
     * @param keyword search keyword
     * @return list of matching categories
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> searchCategories(
            @RequestParam String keyword) {
        List<ServiceCategory> categories = serviceCategoryRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        List<ServiceCategoryResponse> response = categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", response));
    }

    /**
     * Map ServiceCategory entity to ServiceCategoryResponse DTO.
     *
     * @param category ServiceCategory entity
     * @return ServiceCategoryResponse DTO
     */
    private ServiceCategoryResponse mapToResponse(ServiceCategory category) {
        int activeCount = 0;
        if (category.getServiceListings() != null) {
            activeCount = (int) category.getServiceListings().stream()
                    .filter(listing -> listing.getActive() != null && listing.getActive())
                    .count();
        }

        return ServiceCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .slug(category.getSlug())
                .active(category.getActive())
                .displayOrder(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .activeListingsCount(activeCount)
                .build();
    }
}

