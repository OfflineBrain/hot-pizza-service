package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    Optional<ProductCategory> findByName(String name);

    Set<ProductCategory> findAllByParent(ProductCategory parent);
}
