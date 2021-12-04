package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.data.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product  p WHERE lower(p.name) = lower(:name)")
    Optional<Product> findByName(String name);

    List<Product> findAllByCategory(ProductCategory category);

    @Modifying
    @Transactional
    @Query("DELETE FROM Product  p WHERE lower(p.name) = lower(:name)")
    int deleteByName(String name);
}
