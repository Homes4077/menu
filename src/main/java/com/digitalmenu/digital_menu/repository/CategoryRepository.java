package com.digitalmenu.digital_menu.repository;

import com.digitalmenu.digital_menu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByBusinessIdAndActiveTrueOrderByDisplayOrderAsc(Long businessId);
}