package com.digitalmenu.digital_menu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PublicMenuDTO {
    private BusinessDTO business;
    private List<CategoryDTO> categories;

    @Data
    public static class BusinessDTO {
        private String name;
        private String slug;
        private String description;
        private String logoUrl;
        private String coverImageUrl;
        private String phone;
        private String whatsappNumber;
        private String location;
        private String openingHours;
        private String theme;
    }

    @Data
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String description;
        private List<ItemDTO> items;
    }

    @Data
    public static class ItemDTO {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String imageUrl;
        private boolean featured;
    }
}