package com.example.kalban_greenbag.repository;

import com.example.kalban_greenbag.entity.Order;
import com.example.kalban_greenbag.entity.ProductCustomization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductCustomizationRepository extends JpaRepository<ProductCustomization, UUID>{
    List<ProductCustomization> findAllByOrderByCreatedDate(Pageable pageable);
    List<ProductCustomization> findAllByStatusOrderByCreatedDate(String status, Pageable pageable);
    List<ProductCustomization> findAllByUserIdOrderByCreatedDate(UUID userId, Pageable pageable);
    int countByStatus(String status);
}
