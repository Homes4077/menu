package com.digitalmenu.digital_menu.service;

import com.digitalmenu.digital_menu.entity.Business;
import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.model.MenuItem;
import com.digitalmenu.digital_menu.repository.BusinessRepository;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import com.digitalmenu.digital_menu.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final Path uploadPath;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           CategoryRepository categoryRepository,
                           BusinessRepository businessRepository,
                           @Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.businessRepository = businessRepository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(this.uploadPath)) {
            Files.createDirectories(this.uploadPath);
        }
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAllWithCategoryAndBusiness();
    }

    @Transactional(readOnly = true)
    public MenuItem getItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
    }

    public MenuItem saveItem(String name, String description, BigDecimal price, Long categoryId, MultipartFile imageFile) throws IOException {
        Category category = categoryRepository.findById(categoryId).orElseGet(() -> {
            Category newCat = new Category();
            newCat.setId(categoryId);
            
            String catName = switch (categoryId.intValue()) {
                case 1 -> "Morning";
                case 2 -> "Starters";
                case 3 -> "Mains";
                case 4 -> "Desserts";
                case 5 -> "Soft Drinks";
                case 6 -> "Liquor";
                default -> "General";
            };
            newCat.setName(catName);
            newCat.setActive(true);
            newCat.setDisplayOrder(0);

            Business business = businessRepository.findById(1L).orElseGet(() -> {
                Business b = new Business();
                b.setId(1L);
                b.setName("Paleo Hotel & Spa");
                b.setSlug("paleo-hotel-spa");
                b.setActive(true);
                return businessRepository.save(b);
            });
            newCat.setBusiness(business);

            return categoryRepository.save(newCat);
        });

        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);
        item.setAvailable(true);
        item.setFeatured(false);

        if (imageFile != null && !imageFile.isEmpty()) {
            item.setImageUrl(storeFile(imageFile));
        } else {
            item.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80");
        }

        return menuItemRepository.save(item);
    }

    public MenuItem updateItem(Long id, String name, String description, BigDecimal price, Long categoryId, MultipartFile imageFile) throws IOException {
        MenuItem item = getItemById(id);
        
        Category category = categoryRepository.findById(categoryId).orElse(item.getCategory());

        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (item.getImageUrl() != null && item.getImageUrl().startsWith("/images/dishes/")) {
                deleteOldFile(item.getImageUrl());
            }
            item.setImageUrl(storeFile(imageFile));
        }

        return menuItemRepository.save(item);
    }

    public void deleteItem(Long id) {
        MenuItem item = getItemById(id);
        if (item.getImageUrl() != null && item.getImageUrl().startsWith("/images/dishes/")) {
            deleteOldFile(item.getImageUrl());
        }
        menuItemRepository.deleteById(id);
    }

    private String storeFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID().toString() + extension;
        Path targetLocation = this.uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "/images/dishes/" + newFileName;
    }

    private void deleteOldFile(String imageUrl) {
        try {
            String fileName = imageUrl.replace("/images/dishes/", "");
            Path filePath = this.uploadPath.resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}
    }
}
