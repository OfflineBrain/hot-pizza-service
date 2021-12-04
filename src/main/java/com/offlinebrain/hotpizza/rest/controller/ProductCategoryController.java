package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.rest.mapper.entity.CategoryMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.CategoryDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.category.CategoryDTO;
import com.offlinebrain.hotpizza.rest.model.category.CreateCategoryDTO;
import com.offlinebrain.hotpizza.service.ProductCategoryService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final CategoryDTOModelAssembler modelAssembler;
    private final MeterRegistry meterRegistry;

    @Cacheable(value = "productCategories")
    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<CollectionModel<EntityModel<CategoryDTO>>> getAll() {
        List<ProductCategory> productCategories = categoryService.findAll();
        List<CategoryDTO> categories = productCategories.stream()
                .map(categoryMapper::productCategoryToCategoryDto)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<CategoryDTO>> models = modelAssembler.assemble(categories);
        return ResponseEntity.ok(models);
    }

    @Cacheable(value = "productCategory", key = "#name.toLowerCase()")
    @GetMapping(value = "/{name}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<EntityModel<CategoryDTO>> getByName(@PathVariable("name") @NotEmpty String name) {
        Counter.builder("http.request.total")
                .tag("endpoint", "get-category-by-name")
                .register(meterRegistry)
                .increment();

        ProductCategory categoryByName = categoryService.findCategoryByName(name);
        CategoryDTO category = categoryMapper.productCategoryToCategoryDto(categoryByName);
        EntityModel<CategoryDTO> model = modelAssembler.assemble(category);
        return ResponseEntity.ok(model);
    }

    @Cacheable(value = "productCategories", key = "#name.toLowerCase()")
    @GetMapping(value = "/{name}/subcategories", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CollectionModel<EntityModel<CategoryDTO>>> getSubcategories(
            @PathVariable("name") @NotEmpty String name) {
        List<ProductCategory> productCategories = categoryService.findAllSubcategories(name);
        List<CategoryDTO> categories = productCategories.stream()
                .map(categoryMapper::productCategoryToCategoryDto)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<CategoryDTO>> models = modelAssembler.assemble(categories);
        return ResponseEntity.ok(models);
    }

    @Caching(evict = {@CacheEvict(value = "productCategories")},
            put = {@CachePut(value = "productCategory", key = "#category.name.toLowerCase()")})
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<EntityModel<CategoryDTO>> create(@RequestBody @NotNull CreateCategoryDTO category) {
        ProductCategory persistenceEntity = categoryMapper.createCategoryDtoToProductCategory(category);
        ProductCategory createdEntity = categoryService.createCategory(persistenceEntity);

        CategoryDTO createdCategory = categoryMapper.productCategoryToCategoryDto(createdEntity);
        EntityModel<CategoryDTO> model = modelAssembler.assemble(createdCategory);
        return ResponseEntity.created(
                        linkTo(methodOn(ProductCategoryController.class).getByName(category.getName())).toUri())
                .body(model);
    }

    @Caching(evict = {@CacheEvict(value = "productCategories"),
            @CacheEvict(value = "productCategory", key = "#name.toLowerCase()")})
    @DeleteMapping(value = "/{name}")
    @ResponseBody
    public ResponseEntity<Object> delete(@PathVariable("name") @NotEmpty String name) {
        boolean deleted = categoryService.deleteCategoryByName(name);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
