package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.data.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByName(String name);

    Set<Product> findAllByCategory(ProductCategory category);
}
