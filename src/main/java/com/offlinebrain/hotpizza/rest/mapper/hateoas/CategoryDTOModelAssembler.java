package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.ProductCategoryController;
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
                linkTo(methodOn(ProductCategoryController.class).getAll()).withSelfRel());
    }

    public EntityModel<CategoryDTO> assemble(CategoryDTO category) {
        return EntityModel.of(category)
                .add(linkTo(methodOn(ProductCategoryController.class).getByName(category.getName())).withSelfRel());
    }
}
