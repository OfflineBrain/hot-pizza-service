package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.rest.mapper.entity.CategoryMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.CategoryDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.category.CategoryModel;
import com.offlinebrain.hotpizza.rest.model.category.CreateCategoryDTO;
import com.offlinebrain.hotpizza.service.ProductCategoryService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
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
    @GetMapping(produces = "application/hal+json")
    @ResponseBody
    public CollectionModel<CategoryModel> getAll() {
        List<ProductCategory> productCategories = categoryService.findAll();
        List<CategoryModel> categories = productCategories.stream()
                .map(categoryMapper::productCategoryToCategoryDto)
                .collect(Collectors.toList());
        Link self = linkTo(methodOn(ProductCategoryController.class).getAll()).withSelfRel();
        return modelAssembler.assemble(categories)
                .add(self);
    }

    @Cacheable(value = "productCategory", key = "#name.toLowerCase()")
    @GetMapping(value = "/{name}", produces = "application/json")
    @ResponseBody
    public CategoryModel getByName(@PathVariable("name") @NotEmpty String name) {
        Counter.builder("http.request.total")
                .tag("endpoint", "get-category-by-name")
                .register(meterRegistry)
                .increment();

        ProductCategory categoryByName = categoryService.findCategoryByName(name);
        CategoryModel category = categoryMapper.productCategoryToCategoryDto(categoryByName);
        return modelAssembler.assemble(category);
    }

    @Cacheable(value = "productCategories", key = "#name.toLowerCase()")
    @GetMapping(value = "/{name}/subcategories", produces = "application/hal+json")
    @ResponseBody
    public CollectionModel<CategoryModel> getSubcategories(
            @PathVariable("name") @NotEmpty String name) {
        List<ProductCategory> productCategories = categoryService.findAllSubcategories(name);
        List<CategoryModel> categories = productCategories.stream()
                .map(categoryMapper::productCategoryToCategoryDto)
                .collect(Collectors.toList());

        Link self = linkTo(methodOn(ProductCategoryController.class).getSubcategories(name)).withSelfRel();
        return modelAssembler.assemble(categories).add(self);
    }

    @Caching(evict = {@CacheEvict(value = "productCategories")})
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryModel create(@RequestBody @NotNull CreateCategoryDTO category) {
        ProductCategory persistenceEntity = categoryMapper.createCategoryDtoToProductCategory(category);
        ProductCategory createdEntity = categoryService.createCategory(persistenceEntity);

        CategoryModel createdCategory = categoryMapper.productCategoryToCategoryDto(createdEntity);
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
