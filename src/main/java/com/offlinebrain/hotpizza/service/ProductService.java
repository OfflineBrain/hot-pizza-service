package com.offlinebrain.hotpizza.service;

import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.data.repository.ProductCategoryRepository;
import com.offlinebrain.hotpizza.data.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                .orElse(new ArrayList<>());
    }

    public Product findByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(RuntimeException::new);
    }

    public Product create(Product product) {
        return productRepository.saveAndFlush(product);
    }

    public boolean deleteByName(String name) {
        return productRepository.deleteByName(name) == 1;
    }
}
