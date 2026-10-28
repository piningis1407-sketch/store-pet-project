package com.iharshashkou.internetstore.repository;

import com.iharshashkou.internetstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueAndCategory_Id(
            Long categoryId,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
          AND LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    Page<Product> searchActiveProducts(
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            Pageable pageable
    );
}