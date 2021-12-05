package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.ProductCategoryController;
import com.offlinebrain.hotpizza.rest.controller.ProductController;
import com.offlinebrain.hotpizza.rest.model.category.CategoryDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryDTOModelAssembler {
    public CollectionModel<EntityModel<CategoryDTO>> assemble(List<CategoryDTO> categories) {
        List<EntityModel<CategoryDTO>> entityModels = categories.stream()
                .map(this::assemble)
                .collect(Collectors.toList());
        return CollectionModel.of(entityModels,
                linkTo(methodOn(ProductCategoryController.class).getAll()).withRel("all"));
    }

    public EntityModel<CategoryDTO> assemble(CategoryDTO category) {
        EntityModel<CategoryDTO> model = EntityModel.of(category)
                .add(linkTo(methodOn(ProductCategoryController.class).getByName(category.getName())).withSelfRel())
                .add(linkTo(methodOn(ProductController.class).getAllByCategory(category.getName()))
                        .withRel("products"));
        if (category.getParent() != null) {
            model.add(linkTo(methodOn(ProductCategoryController.class).getSubcategories(category.getName()))
                    .withRel("subcategories"));
        }
        return model;
    }
}
