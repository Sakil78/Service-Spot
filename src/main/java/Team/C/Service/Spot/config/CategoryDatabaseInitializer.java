package Team.C.Service.Spot.config;

import Team.C.Service.Spot.model.ServiceCategory;
import Team.C.Service.Spot.repository.ServiceCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Database Initializer for Service Categories.
 * Ensures categories are created with correct explicit IDs (1-17).
 * This prevents random ID assignment and ensures "Others" always has ID 17.
 *
 * @author Team C
 * @version 4.0
 * @since 2025-12-12
 */
@Component
@Order(1) // Run first, before other initializers
@Slf4j
public class CategoryDatabaseInitializer implements CommandLineRunner {

    private final ServiceCategoryRepository categoryRepository;
    private final JdbcTemplate jdbcTemplate;

    public CategoryDatabaseInitializer(ServiceCategoryRepository categoryRepository,
                                       JdbcTemplate jdbcTemplate) {
        this.categoryRepository = categoryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🔧 Initializing Service Categories with explicit IDs...");

        // Define all 17 categories with explicit IDs
        List<CategoryData> categories = Arrays.asList(
            new CategoryData(1L, "Education", "Education and tutoring services", "EDU", 1),
            new CategoryData(2L, "Plumbing", "Plumbing and pipe services", "PLB", 2),
            new CategoryData(3L, "Electrical", "Electrical services and repairs", "ELE", 3),
            new CategoryData(4L, "Cleaning", "Cleaning and housekeeping services", "CLN", 4),
            new CategoryData(5L, "Beauty", "Beauty and wellness services", "BTY", 5),
            new CategoryData(6L, "IT Support", "IT and tech support services", "IT", 6),
            new CategoryData(7L, "Home Repair", "Home repair and maintenance", "HMR", 7),
            new CategoryData(8L, "Health", "Health and medical services", "HLT", 8),
            new CategoryData(9L, "Carpentry", "Carpentry and woodwork", "CRP", 9),
            new CategoryData(10L, "Painting", "Painting services", "PNT", 10),
            new CategoryData(11L, "Appliance Repair", "Repair and maintenance of household appliances", "APR", 11),
            new CategoryData(12L, "Pest Control", "Pest management and extermination services", "PST", 12),
            new CategoryData(13L, "Moving & Delivery", "Transportation and moving services", "MVD", 13),
            new CategoryData(14L, "Gardening", "Lawn care and gardening services", "GRD", 14),
            new CategoryData(15L, "HVAC", "Heating, ventilation, and air conditioning services", "HVC", 15),
            new CategoryData(16L, "Automotive", "Car repair and maintenance services", "AUT", 16),
            new CategoryData(17L, "Others", "All other services not categorized above", "OTH", 17)
        );

        int created = 0;
        int existing = 0;

        for (CategoryData data : categories) {
            // Check if category with this ID exists
            if (!categoryRepository.existsById(data.id)) {
                // Use native SQL to insert with explicit ID
                try {
                    String sql = "INSERT INTO service_categories (id, name, description, icon, active, display_order, slug, created_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    jdbcTemplate.update(sql,
                            data.id,
                            data.name,
                            data.description,
                            data.icon,
                            true,
                            data.displayOrder,
                            generateSlug(data.name),
                            LocalDateTime.now());

                    log.info("✅ Created category: {} (ID: {})", data.name, data.id);
                    created++;
                } catch (Exception e) {
                    log.error("❌ Failed to create category {} (ID: {}): {}", data.name, data.id, e.getMessage());
                }
            } else {
                log.debug("✓ Category exists: {} (ID: {})", data.name, data.id);
                existing++;
            }
        }

        // Verify "Others" has ID 17
        ServiceCategory othersCategory = categoryRepository.findByName("Others").orElse(null);
        if (othersCategory != null) {
            if (othersCategory.getId().equals(17L)) {
                log.info("✅ 'Others' category correctly has ID 17 - Automatic assignment will work!");
            } else {
                log.error("❌ CRITICAL: 'Others' category has WRONG ID: {} (expected: 17)", othersCategory.getId());
                log.error("❌ This will cause automatic assignment to fail!");
                log.error("❌ Attempting to fix...");

                // Try to fix it
                try {
                    // Delete the wrong "Others"
                    jdbcTemplate.update("DELETE FROM service_categories WHERE name = 'Others' AND id != 17");

                    // Insert correct "Others" with ID 17 if not exists
                    if (!categoryRepository.existsById(17L)) {
                        String sql = "INSERT INTO service_categories (id, name, description, icon, active, display_order, slug, created_at) " +
                                    "VALUES (17, 'Others', 'All other services not categorized above', 'OTH', true, 17, 'others', ?)";
                        jdbcTemplate.update(sql, LocalDateTime.now());
                        log.info("✅ Fixed: Created 'Others' with correct ID 17");
                    }
                } catch (Exception e) {
                    log.error("❌ Failed to fix 'Others' category: {}", e.getMessage());
                }
            }
        } else {
            log.error("❌ CRITICAL: 'Others' category not found! Creating it now...");
            try {
                String sql = "INSERT INTO service_categories (id, name, description, icon, active, display_order, slug, created_at) " +
                            "VALUES (17, 'Others', 'All other services not categorized above', 'OTH', true, 17, 'others', ?)";
                jdbcTemplate.update(sql, LocalDateTime.now());
                log.info("✅ Created 'Others' category with ID 17");
                created++;
            } catch (Exception e) {
                log.error("❌ Failed to create 'Others' category: {}", e.getMessage());
            }
        }

        log.info("🎯 Category initialization complete: {} created, {} existing, {} total",
                created, existing, categories.size());
        log.info("🎉 GitHub users will now have correct category IDs automatically!");
    }

    /**
     * Generate URL-friendly slug from category name
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Internal class to hold category data
     */
    private static class CategoryData {
        Long id;
        String name;
        String description;
        String icon;
        int displayOrder;

        CategoryData(Long id, String name, String description, String icon, int displayOrder) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.displayOrder = displayOrder;
        }
    }
}

