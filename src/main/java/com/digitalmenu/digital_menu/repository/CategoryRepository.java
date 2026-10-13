package com.digitalmenu.digital_menu.repository;

import com.digitalmenu.digital_menu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByBusinessIdAndActiveTrueOrderByDisplayOrderAsc(Long businessId);
}
