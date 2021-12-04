package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.rest.mapper.entity.ProductMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.ProductDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.product.CreateProductDTO;
import com.offlinebrain.hotpizza.rest.model.product.ProductDTO;
import com.offlinebrain.hotpizza.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final ProductDTOModelAssembler modelAssembler;

    @Cacheable(value = "products")
    @GetMapping(value = "/products", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CollectionModel<EntityModel<ProductDTO>>> getAll() {
        List<Product> all = productService.findAll();
        List<ProductDTO> productDTOs = all.stream().map(productMapper::productToProductDto).toList();
        CollectionModel<EntityModel<ProductDTO>> models = modelAssembler.assemble(productDTOs);
        return ResponseEntity.ok(models);
    }

    @Cacheable(value = "products", key = "#category.toLowerCase()")
    @GetMapping(value = "/categories/{category}/products", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CollectionModel<EntityModel<ProductDTO>>> getAllByCategory(
            @PathVariable @NotEmpty String category) {
        List<Product> all = productService.findByCategoryName(category);
        List<ProductDTO> productDTOs = all.stream().map(productMapper::productToProductDto).toList();
        CollectionModel<EntityModel<ProductDTO>> models = modelAssembler.assemble(productDTOs);
        return ResponseEntity.ok(models);
    }

    @Cacheable(value = "product", key = "#name.toLowerCase()")
    @GetMapping(value = "/products/{name}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<EntityModel<ProductDTO>> getByName(@PathVariable @NotEmpty String name) {
        Product product = productService.findByName(name);
        EntityModel<ProductDTO> model = modelAssembler.assemble(productMapper.productToProductDto(product));
        return ResponseEntity.ok(model);
    }

    @Caching(evict = {@CacheEvict("products")},
            put = {@CachePut(value = "product", key = "#dto.name.toLowerCase()")})
    @PostMapping(value = "/products", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<EntityModel<ProductDTO>> createProduct(@RequestBody CreateProductDTO dto) {
        Product saved = productService.create(productMapper.createProductDtoToProduct(dto));
        EntityModel<ProductDTO> model = modelAssembler.assemble(productMapper.productToProductDto(saved));
        return ResponseEntity.created(linkTo(methodOn(ProductController.class).getByName(dto.getName())).toUri())
                .body(model);
    }

    @Caching(evict = {@CacheEvict(value = "products"),
            @CacheEvict(value = "product", key = "#name.toLowerCase()")})
    @DeleteMapping(value = "/products/{name}")
    @ResponseBody
    public ResponseEntity<Object> deleteProductByName(@PathVariable @NotEmpty String name) {
        boolean deleted = productService.deleteByName(name);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
