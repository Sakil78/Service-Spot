package Team.C.Service.Spot.config;

import Team.C.Service.Spot.model.ServiceCategory;
import Team.C.Service.Spot.model.ServiceListing;
import Team.C.Service.Spot.repository.ServiceCategoryRepository;
import Team.C.Service.Spot.repository.ServiceListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Database initialization component that runs on application startup.
 * Ensures data integrity and fixes common issues automatically.
 *
 * @author Team C
 * @version 4.0
 * @since 2025-12-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final ServiceCategoryRepository categoryRepository;
    private final ServiceListingRepository listingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Starting database initialization...");

        // 1. Ensure "Others" category exists
        ensureOthersCategoryExists();

        // 2. Fix orphaned services (services without category)
        fixOrphanedServices();

        // 3. Ensure all predefined categories exist
        ensurePredefinedCategoriesExist();

        log.info("✅ Database initialization complete!");
    }

    /**
     * Ensure "Others" category exists in the database.
     * Creates it if missing.
     */
    private void ensureOthersCategoryExists() {
        try {
            ServiceCategory others = categoryRepository.findByName("Others")
                    .orElseGet(() -> {
                        log.info("📁 Creating 'Others' category...");
                        ServiceCategory newOthers = ServiceCategory.builder()
                                .name("Others")
                                .description("All other services not categorized above")
                                .icon("OTH")
                                .active(true)
                                .displayOrder(17)
                                .build();
                        return categoryRepository.save(newOthers);
                    });
            log.info("✅ 'Others' category ready (ID: {})", others.getId());
        } catch (Exception e) {
            log.error("❌ Failed to ensure 'Others' category exists", e);
        }
    }

    /**
     * Fix services that have no category assigned.
     * Assigns them to "Others" category automatically.
     * Uses individual ID-based updates to work with MySQL safe mode.
     */
    private void fixOrphanedServices() {
        try {
            // Find all services without a category
            List<ServiceListing> orphanedServices = listingRepository.findAll().stream()
                    .filter(service -> service.getCategory() == null)
                    .toList();

            if (orphanedServices.isEmpty()) {
                log.info("✅ No orphaned services found");
                return;
            }

            log.info("🔧 Found {} orphaned service(s) - assigning to 'Others'...", orphanedServices.size());

            // Get or create Others category
            ServiceCategory others = categoryRepository.findByName("Others")
                    .orElseGet(() -> {
                        log.info("📁 Creating 'Others' category (not found in database)...");
                        ServiceCategory newOthers = ServiceCategory.builder()
                                .name("Others")
                                .description("All other services not categorized above")
                                .icon("OTH")
                                .active(true)
                                .displayOrder(17)
                                .build();
                        ServiceCategory saved = categoryRepository.save(newOthers);
                        log.info("✅ Created 'Others' category with ID: {}", saved.getId());
                        return saved;
                    });

            // Assign each orphaned service to Others individually
            // This approach works with MySQL safe update mode
            int fixedCount = 0;
            for (ServiceListing service : orphanedServices) {
                try {
                    service.setCategory(others);
                    // Also ensure the service is active
                    if (!service.getActive()) {
                        service.setActive(true);
                        log.info("  ℹ️ Also activated service '{}'", service.getTitle());
                    }
                    listingRepository.save(service);
                    log.info("  ✓ Assigned '{}' (ID: {}) to 'Others' category",
                            service.getTitle(), service.getId());
                    fixedCount++;
                } catch (Exception e) {
                    log.error("  ❌ Failed to fix service '{}' (ID: {}): {}",
                            service.getTitle(), service.getId(), e.getMessage());
                }
            }

            log.info("✅ Fixed {} orphaned service(s)", fixedCount);
        } catch (Exception e) {
            log.error("❌ Failed to fix orphaned services", e);
        }
    }

    /**
     * Ensure all predefined categories exist in the database.
     * Creates missing ones automatically.
     */
    private void ensurePredefinedCategoriesExist() {
        try {
            List<String> predefinedCategories = List.of(
                "Education", "Plumbing", "Electrical", "Cleaning", "Beauty",
                "IT Support", "Home Repair", "Health", "Carpentry", "Painting",
                "Appliance Repair", "Pest Control", "Moving & Delivery",
                "Gardening", "HVAC", "Automotive"
            );

            int created = 0;
            for (int i = 0; i < predefinedCategories.size(); i++) {
                String categoryName = predefinedCategories.get(i);
                if (categoryRepository.findByName(categoryName).isEmpty()) {
                    ServiceCategory category = ServiceCategory.builder()
                            .name(categoryName)
                            .description(categoryName + " services")
                            .icon(getCategoryIcon(categoryName))
                            .active(true)
                            .displayOrder(i + 1)
                            .build();
                    categoryRepository.save(category);
                    created++;
                }
            }

            if (created > 0) {
                log.info("✅ Created {} missing predefined categor(ies)", created);
            } else {
                log.info("✅ All predefined categories already exist");
            }
        } catch (Exception e) {
            log.error("❌ Failed to ensure predefined categories exist", e);
        }
    }

    /**
     * Get icon for a category name.
     */
    private String getCategoryIcon(String categoryName) {
        return switch (categoryName.toUpperCase()) {
            case "EDUCATION" -> "EDU";
            case "PLUMBING" -> "PLU";
            case "ELECTRICAL" -> "ELE";
            case "CLEANING" -> "CLN";
            case "BEAUTY" -> "BTY";
            case "IT SUPPORT" -> "ITS";
            case "HOME REPAIR" -> "HMR";
            case "HEALTH" -> "HLT";
            case "CARPENTRY" -> "CRP";
            case "PAINTING" -> "PNT";
            case "APPLIANCE REPAIR" -> "APR";
            case "PEST CONTROL" -> "PST";
            case "MOVING & DELIVERY", "MOVING AND DELIVERY" -> "MVD";
            case "GARDENING" -> "GRD";
            case "HVAC" -> "HVC";
            case "AUTOMOTIVE" -> "AUT";
            default -> "OTH";
        };
    }
}

