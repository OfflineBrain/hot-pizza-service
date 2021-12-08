package com.offlinebrain.hotpizza.service;

import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.data.repository.ProductCategoryRepository;
import com.offlinebrain.hotpizza.exception.ResourceExistsException;
import com.offlinebrain.hotpizza.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository categoryRepository;

    public List<ProductCategory> findAll() {
        return categoryRepository.findAll();
    }

    public List<ProductCategory> findAllSubcategories(String name) {
        return categoryRepository.findByName(name)
                .map(categoryRepository::findAllByParent)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "name", name));
    }

    public ProductCategory createCategory(ProductCategory category) {
        if (Objects.nonNull(category.getParent())) {
            categoryRepository.findById(category.getParent().getUuid())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "ID",
                            category.getParent().getUuid().toString()));
        }
        categoryRepository.findByName(category.getName())
                .ifPresent(productCategory -> {
                    throw new ResourceExistsException("ProductCategory", "name", productCategory.getName());
                });
        return categoryRepository.save(category);
    }

    public ProductCategory findCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "name", name));
    }

    public boolean deleteCategoryByName(String name) {
        categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "name", name));
        return categoryRepository.deleteByName(name) == 1;
    }
}
