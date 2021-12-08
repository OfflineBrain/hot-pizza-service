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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;


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
    public CollectionModel<EntityModel<CategoryDTO>> getAll() {
        List<ProductCategory> productCategories = categoryService.findAll();
        List<CategoryDTO> categories = productCategories.stream()
                .map(categoryMapper::productCategoryToCategoryDto)
                .collect(Collectors.toList());
        return modelAssembler.assemble(categories);
    }

    @Cacheable(value = "productCategory", key = "#name.toLowerCase()")
    @GetMapping(value = "/{name}", produces = "application/json")
    @ResponseBody
    public EntityModel<CategoryDTO> getByName(@PathVariable("name") @NotEmpty String name) {
        Counter.builder("http.request.total")
                .tag("endpoint", "get-category-by-name")
                .register(meterRegistry)
                .increment();

        ProductCategory categoryByName = categoryService.findCategoryByName(name);
        CategoryDTO category = categoryMapper.productCategoryToCategoryDto(categoryByName);
        return modelAssembler.assemble(category);
    }

    @Cacheable(value = "productCategories", key = "#name.toLowerCase()")
    @GetMapping(value = "/{name}/subcategories", produces = "application/json")
    @ResponseBody
    public CollectionModel<EntityModel<CategoryDTO>> getSubcategories(
            @PathVariable("name") @NotEmpty String name) {
        List<ProductCategory> productCategories = categoryService.findAllSubcategories(name);
        List<CategoryDTO> categories = productCategories.stream()
                .map(categoryMapper::productCategoryToCategoryDto)
                .collect(Collectors.toList());
        return modelAssembler.assemble(categories);
    }

    @Caching(evict = {@CacheEvict(value = "productCategories")})
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<CategoryDTO> create(@RequestBody @NotNull CreateCategoryDTO category) {
        ProductCategory persistenceEntity = categoryMapper.createCategoryDtoToProductCategory(category);
        ProductCategory createdEntity = categoryService.createCategory(persistenceEntity);

        CategoryDTO createdCategory = categoryMapper.productCategoryToCategoryDto(createdEntity);
        return modelAssembler.assemble(createdCategory);
    }

    @Caching(evict = {@CacheEvict(value = "productCategories"),
            @CacheEvict(value = "productCategory", key = "#name.toLowerCase()")})
    @DeleteMapping(value = "/{name}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("name") @NotEmpty String name) {
        categoryService.deleteCategoryByName(name);
    }
}
