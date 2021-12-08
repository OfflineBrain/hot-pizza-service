package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.rest.mapper.entity.ProductMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.ProductDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.product.CreateProductDTO;
import com.offlinebrain.hotpizza.rest.model.product.ProductModel;
import com.offlinebrain.hotpizza.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final ProductDTOModelAssembler modelAssembler;

    @Cacheable(value = "products")
    @GetMapping(value = "/products", produces = "application/json")
    @ResponseBody
    public CollectionModel<ProductModel> getAll() {
        List<Product> all = productService.findAll();
        List<ProductModel> productModels = all.stream().map(productMapper::productToProductDto).toList();
        return modelAssembler.assemble(productModels);
    }

    @Cacheable(value = "products", key = "#category.toLowerCase()")
    @GetMapping(value = "/categories/{category}/products", produces = "application/json")
    @ResponseBody
    public CollectionModel<ProductModel> getAllByCategory(
            @PathVariable @NotEmpty String category) {
        List<Product> all = productService.findByCategoryName(category);
        List<ProductModel> productModels = all.stream().map(productMapper::productToProductDto).toList();
        return modelAssembler.assemble(productModels);
    }

    @Cacheable(value = "product", key = "#name.toLowerCase()")
    @GetMapping(value = "/products/{name}", produces = "application/json")
    @ResponseBody
    public ProductModel getByName(@PathVariable @NotEmpty String name) {
        Product product = productService.findByName(name);
        return modelAssembler.assemble(productMapper.productToProductDto(product));
    }

    @Caching(evict = {@CacheEvict("products")})
    @PostMapping(value = "/products", consumes = "application/json", produces = "application/json")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.CREATED)
    public ProductModel createProduct(@RequestBody CreateProductDTO dto) {
        Product saved = productService.create(productMapper.createProductDtoToProduct(dto));
        return modelAssembler.assemble(productMapper.productToProductDto(saved));
    }

    @Caching(evict = {@CacheEvict(value = "products"),
            @CacheEvict(value = "product", key = "#name.toLowerCase()")})
    @DeleteMapping(value = "/products/{name}")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteProductByName(@PathVariable @NotEmpty String name) {
        productService.deleteByName(name);
    }
}
