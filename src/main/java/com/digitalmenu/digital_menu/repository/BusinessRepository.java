package com.digitalmenu.digital_menu.repository;

import com.digitalmenu.digital_menu.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    Optional<Business> findBySlug(String slug);

    Optional<Business> findBySlugAndActiveTrue(String slug);
}
