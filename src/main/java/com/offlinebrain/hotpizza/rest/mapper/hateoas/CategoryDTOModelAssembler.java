package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.ProductCategoryController;
import com.offlinebrain.hotpizza.rest.controller.ProductController;
import com.offlinebrain.hotpizza.rest.model.category.CategoryModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryDTOModelAssembler {
    public CollectionModel<CategoryModel> assemble(List<CategoryModel> categories) {
        List<CategoryModel> entityModels = categories.stream().map(this::assemble).collect(Collectors.toList());
        Link allLink = linkTo(methodOn(ProductCategoryController.class).getAll()).withRel("all");
        return CollectionModel.of(entityModels)
                .add(allLink);
    }

    public CategoryModel assemble(CategoryModel category) {
        category.add(linkTo(methodOn(ProductCategoryController.class).getByName(category.getName())).withSelfRel())
                .add(linkTo(methodOn(ProductController.class).getAllByCategory(category.getName()))
                        .withRel("products"));
        if (category.getParent() != null) {
            category.add(linkTo(methodOn(ProductCategoryController.class).getSubcategories(category.getName()))
                    .withRel("subcategories"));
        }
        return category;
    }
}
