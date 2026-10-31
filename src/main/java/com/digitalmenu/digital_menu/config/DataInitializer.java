package com.digitalmenu.digital_menu.config;

import com.digitalmenu.digital_menu.entity.Business;
import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.repository.BusinessRepository;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeCategories(BusinessRepository businessRepository,
                                                  CategoryRepository categoryRepository) {
        return args -> {
            // 1. Ensure the default business exists
            String slug = "paleo-hotel-spa";
            Business business = businessRepository.findBySlug(slug).orElseGet(() -> {
                Business newBusiness = new Business();
                newBusiness.setName("Paleo Hotel & Spa");
                newBusiness.setSlug(slug);
                newBusiness.setDescription("Experience luxury dining and relaxation.");
                newBusiness.setWhatsappNumber("+254700000000");
                newBusiness.setActive(true);
                return businessRepository.save(newBusiness);
            });

            // 2. Define the exact 6 categories required
            List<String> requiredCategories = List.of(
                    "Morning", 
                    "Starters", 
                    "Main", 
                    "Desserts", 
                    "Soft Drinks", 
                    "Liquor (18+)"
            );

            // 3. Check and seed missing categories safely
            int order = 1;
            for (String catName : requiredCategories) {
                boolean exists = categoryRepository.findByBusinessIdAndActiveTrueOrderByDisplayOrderAsc(business.getId())
                        .stream()
                        .anyMatch(c -> c.getName().equalsIgnoreCase(catName));

                if (!exists) {
                    Category category = new Category();
                    category.setName(catName);
                    category.setDescription("Freshly prepared " + catName.toLowerCase());
                    category.setBusiness(business);
                    category.setActive(true);
                    category.setDisplayOrder(order);
                    categoryRepository.save(category);
                    System.out.println("[DataInitializer] Seeded category: " + catName);
                }
                order++;
            }

            // 4. Verification step: Log all active categories stored in the database
            System.out.println("=== VERIFYING DATABASE CATEGORIES ON STARTUP ===");
            categoryRepository.findByBusinessIdAndActiveTrueOrderByDisplayOrderAsc(business.getId()).forEach(cat -> {
                System.out.println("-> Category ID: " + cat.getId() + " | Name: " + cat.getName() + " | Order: " + cat.getDisplayOrder());
            });
            System.out.println("================================================");
        };
    }
}
