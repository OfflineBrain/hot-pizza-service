package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

    @Query("SELECT pc FROM ProductCategory pc WHERE lower(pc.name) = lower(:name)")
    Optional<ProductCategory> findByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductCategory pc WHERE lower(pc.name) = lower(:name)")
    int deleteByName(String name);

    List<ProductCategory> findAllByParent(ProductCategory parent);
}
