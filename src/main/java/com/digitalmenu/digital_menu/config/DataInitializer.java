package com.digitalmenu.digital_menu.config;

import com.digitalmenu.digital_menu.entity.Business;
import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.repository.BusinessRepository;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initializeCategories(BusinessRepository businessRepository,
                                                  CategoryRepository categoryRepository) {
        return argsString -> {
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

            // The exact 6 active categories required
            List<String> requiredCategories = List.of(
                    "Morning", 
                    "Starters", 
                    "Day's Specials", 
                    "Desserts", 
                    "Soft Drinks", 
                    "Liquor (18+)"
            );

            List<Category> currentCategories = categoryRepository.findByBusinessIdAndActiveTrueOrderByDisplayOrderAsc(business.getId());

            int order = 1;
            for (String catName : requiredCategories) {
                Category category = currentCategories.stream()
                        .filter(c -> c.getName().equalsIgnoreCase(catName))
                        .findFirst()
                        .orElse(null);

                if (category == null) {
                    category = new Category();
                    category.setName(catName);
                    category.setDescription("Freshly prepared " + catName.toLowerCase());
                    category.setBusiness(business);
                    category.setActive(true);
                }
                
                category.setDisplayOrder(order++);
                categoryRepository.save(category);
            }

            // Verification log
            System.out.println("=== VERIFYING DATABASE CATEGORIES ON STARTUP ===");
            categoryRepository.findByBusinessIdAndActiveTrueOrderByDisplayOrderAsc(business.getId()).forEach(cat -> {
                System.out.println("-> Category ID: " + cat.getId() + " | Name: " + cat.getName() + " | Order: " + cat.getDisplayOrder());
            });
            System.out.println("================================================");
        };
    }
}
