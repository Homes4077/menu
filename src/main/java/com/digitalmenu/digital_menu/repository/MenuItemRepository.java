package com.digitalmenu.digital_menu.repository;

import com.digitalmenu.digital_menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryIdAndAvailableTrueOrderByDisplayOrderAsc(Long categoryId);
}