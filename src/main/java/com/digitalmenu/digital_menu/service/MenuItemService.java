package com.digitalmenu.digital_menu.service;

import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.model.MenuItem;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import com.digitalmenu.digital_menu.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final Path uploadPath;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           CategoryRepository categoryRepository,
                           @Value("${app.upload.dir}") String uploadDir) throws IOException {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(this.uploadPath)) {
            Files.createDirectories(this.uploadPath);
        }
    }

    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem getItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
    }

    public MenuItem saveItem(String name, String description, BigDecimal price, Long categoryId, MultipartFile imageFile) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            item.setImageUrl(storeFile(imageFile));
        }

        return menuItemRepository.save(item);
    }

    public MenuItem updateItem(Long id, String name, String description, BigDecimal price, Long categoryId, MultipartFile imageFile) throws IOException {
        MenuItem item = getItemById(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (item.getImageUrl() != null) {
                deleteOldFile(item.getImageUrl());
            }
            item.setImageUrl(storeFile(imageFile));
        }

        return menuItemRepository.save(item);
    }

    public void deleteItem(Long id) {
        MenuItem item = getItemById(id);
        if (item.getImageUrl() != null) {
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