package com.digitalmenu.digital_menu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String phone;

    @Column(nullable = false)
    private String whatsappNumber;

    private String email;
    private String location;
    private String openingHours;

    @Column(nullable = false)
    private String theme = "LUXURY";

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}