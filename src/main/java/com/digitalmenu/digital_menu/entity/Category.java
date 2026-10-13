package com.digitalmenu.digital_menu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "categories", "menuItems"})
    private Business business;

    @Column(nullable = false)
    private String name;

    private String description;
    private Integer displayOrder = 0;
    private boolean active = true;
}
