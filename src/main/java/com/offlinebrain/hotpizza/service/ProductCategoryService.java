package com.offlinebrain.hotpizza.service;

import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.data.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductCategoryService {
    private final ProductCategoryRepository categoryRepository;

    public ProductCategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ProductCategory> findAll() {
        return categoryRepository.findAll();
    }

    public List<ProductCategory> findAllSubcategories(String name) {
        return categoryRepository.findByName(name)
                .map(categoryRepository::findAllByParent)
                .orElse(new ArrayList<>());
    }

    public ProductCategory createCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    public ProductCategory findCategoryByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(RuntimeException::new);
    }

    public boolean deleteCategoryByName(String name) {
        return categoryRepository.deleteByName(name) == 1;
    }
}
