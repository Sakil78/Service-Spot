-- Insert default service categories with EXPLICIT IDs
-- This ensures "Others" always gets ID 17, preventing random ID assignment
INSERT INTO service_categories (id, name, description, icon, active, created_at, display_order) VALUES
(1, 'Education', 'Education and tutoring services', 'EDU', true, NOW(), 1),
(2, 'Plumbing', 'Plumbing and pipe services', 'PLB', true, NOW(), 2),
(3, 'Electrical', 'Electrical services and repairs', 'ELE', true, NOW(), 3),
(4, 'Cleaning', 'Cleaning and housekeeping services', 'CLN', true, NOW(), 4),
(5, 'Beauty', 'Beauty and wellness services', 'BTY', true, NOW(), 5),
(6, 'IT Support', 'IT and tech support services', 'IT', true, NOW(), 6),
(7, 'Home Repair', 'Home repair and maintenance', 'HMR', true, NOW(), 7),
(8, 'Health', 'Health and medical services', 'HLT', true, NOW(), 8),
(9, 'Carpentry', 'Carpentry and woodwork', 'CRP', true, NOW(), 9),
(10, 'Painting', 'Painting services', 'PNT', true, NOW(), 10),
(11, 'Appliance Repair', 'Repair and maintenance of household appliances', 'APR', true, NOW(), 11),
(12, 'Pest Control', 'Pest management and extermination services', 'PST', true, NOW(), 12),
(13, 'Moving & Delivery', 'Transportation and moving services', 'MVD', true, NOW(), 13),
(14, 'Gardening', 'Lawn care and gardening services', 'GRD', true, NOW(), 14),
(15, 'HVAC', 'Heating, ventilation, and air conditioning services', 'HVC', true, NOW(), 15),
(16, 'Automotive', 'Car repair and maintenance services', 'AUT', true, NOW(), 16),
(17, 'Others', 'All other services not categorized above', 'OTH', true, NOW(), 17)
ON DUPLICATE KEY UPDATE name=name;

-- Note: Admin account is automatically created by AdminInitializer.java on startup
-- Email: admin@servicespot.com
-- Password: Admin@123
