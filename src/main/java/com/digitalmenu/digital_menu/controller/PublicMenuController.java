package com.digitalmenu.digital_menu.controller;

import com.digitalmenu.digital_menu.dto.PublicMenuDTO;
import com.digitalmenu.digital_menu.entity.Business;
import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.model.MenuItem;
import com.digitalmenu.digital_menu.repository.BusinessRepository;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import com.digitalmenu.digital_menu.repository.MenuItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/menu")
@CrossOrigin(origins = "*")
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
        Business business = businessRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new RuntimeException("Business not found or inactive: " + slug));

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
    }
}