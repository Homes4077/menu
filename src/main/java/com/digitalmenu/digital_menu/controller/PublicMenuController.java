package com.digitalmenu.digital_menu.controller;

import com.digitalmenu.digital_menu.dto.PublicMenuDTO;
import com.digitalmenu.digital_menu.entity.Business;
import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.model.MenuItem;
import com.digitalmenu.digital_menu.repository.BusinessRepository;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import com.digitalmenu.digital_menu.repository.MenuItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/menu")
@CrossOrigin(origins = "*")
@Transactional
public class PublicMenuController {

    private final BusinessRepository businessRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public PublicMenuController(BusinessRepository businessRepository,
                                CategoryRepository categoryRepository,
                                MenuItemRepository menuItemRepository) {
        this.businessRepository = businessRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PublicMenuDTO> getPublicMenu(@PathVariable String slug) {
        try {
            Optional<Business> optionalBusiness = businessRepository.findBySlug(slug);
            Business business;

            if (optionalBusiness.isPresent()) {
                business = optionalBusiness.get();
            } else {
                // Create business record
                Business newBusiness = new Business();
                newBusiness.setName("Paleo Hotel & Spa");
                newBusiness.setSlug(slug);
                newBusiness.setDescription("Experience luxury dining and relaxation.");
                newBusiness.setWhatsappNumber("+254700000000");
                newBusiness.setActive(true);
                business = businessRepository.save(newBusiness);

                // Auto-seed default categories & sample dishes for a fully populated menu
                Category morning = new Category();
                morning.setName("Morning");
                morning.setDescription("Breakfast & morning specials");
                morning.setBusinessId(business.getId());
                morning.setActive(true);
                morning.setDisplayOrder(1);
                categoryRepository.save(morning);

                MenuItem item1 = new MenuItem();
                item1.setName("Classic Avocado Toast");
                item1.setDescription("Sourdough bread with fresh avocado, poached eggs, and microgreens.");
                item1.setPrice(BigDecimal.valueOf(850.00));
                item1.setCategoryId(morning.getId());
                item1.setAvailable(true);
                item1.setFeatured(true);
                item1.setDisplayOrder(1);
                menuItemRepository.save(item1);

                Category starters = new Category();
                starters.setName("Starters");
                starters.setDescription("Light bites to begin your meal");
                starters.setBusinessId(business.getId());
                starters.setActive(true);
                starters.setDisplayOrder(2);
                categoryRepository.save(starters);

                MenuItem item2 = new MenuItem();
                item2.setName("Crispy Calamari");
                item2.setDescription("Tender calamari served with zesty lemon garlic aioli.");
                item2.setPrice(BigDecimal.valueOf(1200.00));
                item2.setCategoryId(starters.getId());
                item2.setAvailable(true);
                item2.setFeatured(false);
                item2.setDisplayOrder(1);
                menuItemRepository.save(item2);
            }

            if (!business.isActive()) {
                business.setActive(true);
                business = businessRepository.save(business);
            }

            PublicMenuDTO menuDTO = new PublicMenuDTO();

            PublicMenuDTO.BusinessDTO bDto = new PublicMenuDTO.BusinessDTO();
            bDto.setName(business.getName());
            bDto.setSlug(business.getSlug());
            bDto.setDescription(business.getDescription());
            bDto.setLogoUrl(business.getLogoUrl());
            bDto.setCoverImageUrl(business.getCoverImageUrl());
            bDto.setPhone(business.getPhone());
            bDto.setWhatsappNumber(business.getWhatsappNumber());
            bDto.setLocation(business.getLocation());
            bDto.setOpeningHours(business.getOpeningHours());
            bDto.setTheme(business.getTheme());
            menuDTO.setBusiness(bDto);

            List<Category> categories = categoryRepository.findByBusinessIdAndActiveTrueOrderByDisplayOrderAsc(business.getId());

            List<PublicMenuDTO.CategoryDTO> categoryDTOs = categories.stream().map(cat -> {
                PublicMenuDTO.CategoryDTO cDto = new PublicMenuDTO.CategoryDTO();
                cDto.setId(cat.getId());
                cDto.setName(cat.getName());
                cDto.setDescription(cat.getDescription());

                List<MenuItem> items = menuItemRepository.findByCategoryIdAndAvailableTrueOrderByDisplayOrderAsc(cat.getId());
                List<PublicMenuDTO.ItemDTO> itemDTOs = items.stream().map(item -> {
                    PublicMenuDTO.ItemDTO iDto = new PublicMenuDTO.ItemDTO();
                    iDto.setId(item.getId());
                    iDto.setName(item.getName());
                    iDto.setDescription(item.getDescription());
                    iDto.setPrice(item.getPrice());
                    iDto.setImageUrl(item.getImageUrl());
                    iDto.setFeatured(item.isFeatured());
                    return iDto;
                }).collect(Collectors.toList());

                cDto.setItems(itemDTOs);
                return cDto;
            }).collect(Collectors.toList());

            menuDTO.setCategories(categoryDTOs);

            return ResponseEntity.ok(menuDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
