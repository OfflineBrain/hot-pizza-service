package com.offlinebrain.hotpizza.service;

import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.data.repository.ProductCategoryRepository;
import com.offlinebrain.hotpizza.data.repository.ProductRepository;
import com.offlinebrain.hotpizza.exception.ResourceExistsException;
import com.offlinebrain.hotpizza.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByCategoryName(String name) {
        Optional<ProductCategory> category = categoryRepository.findByName(name);
        return category.map(ProductCategory::getProducts)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "name", name));
    }

    public Product findByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "name", name));
    }

    public Product create(Product product) {
        productRepository.findByName(product.getName())
                .ifPresent(productCategory -> {
                    throw new ResourceExistsException("Product", "name", productCategory.getName());
                });
        categoryRepository.findById(product.getCategory().getUuid())
                .orElseThrow(() ->
                        new ResourceNotFoundException("ProductCategory", "ID",
                                product.getCategory().getUuid().toString())
                );
        return productRepository.saveAndFlush(product);
    }

    public boolean deleteByName(String name) {
        return productRepository.deleteByName(name) == 1;
    }
}
