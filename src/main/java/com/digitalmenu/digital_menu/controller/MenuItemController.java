package com.digitalmenu.digital_menu.controller;

import com.digitalmenu.digital_menu.entity.Category;
import com.digitalmenu.digital_menu.model.MenuItem;
import com.digitalmenu.digital_menu.repository.CategoryRepository;
import com.digitalmenu.digital_menu.repository.MenuItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Base64;

@RestController
@RequestMapping("/api/menu-items")
@CrossOrigin(origins = "*")
public class MenuItemController {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuItemController(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<?> createMenuItem(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            MenuItem item = new MenuItem();
            item.setName(name);
            item.setDescription(description != null ? description : "");
            item.setPrice(price);
            item.setAvailable(true);

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
            item.setCategory(category);

            // Convert uploaded image to Base64 data string for persistent database storage
            if (image != null && !image.isEmpty()) {
                String base64Image = "data:" + image.getContentType() + ";base64," + Base64.getEncoder().encodeToString(image.getBytes());
                item.setImageUrl(base64Image);
            } else {
                item.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80");
            }

            MenuItem savedItem = menuItemRepository.save(item);
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            MenuItem item = menuItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            item.setName(name);
            item.setDescription(description != null ? description : "");
            item.setPrice(price);

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
            item.setCategory(category);

            if (image != null && !image.isEmpty()) {
                String base64Image = "data:" + image.getContentType() + ";base64," + Base64.getEncoder().encodeToString(image.getBytes());
                item.setImageUrl(base64Image);
            }

            MenuItem updatedItem = menuItemRepository.save(item);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        menuItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllItems() {
        return ResponseEntity.ok(menuItemRepository.findAll());
    }
}
