package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.ProductCategoryController;
import com.offlinebrain.hotpizza.rest.controller.ProductController;
import com.offlinebrain.hotpizza.rest.model.product.ProductDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductDTOModelAssembler {
    public CollectionModel<EntityModel<ProductDTO>> assemble(List<ProductDTO> dto) {
        List<EntityModel<ProductDTO>> entityModels = dto.stream()
                .map(this::assemble)
                .collect(Collectors.toList());
        return CollectionModel.of(entityModels,
                linkTo(methodOn(ProductController.class).getAll()).withRel("all"));
    }

    public EntityModel<ProductDTO> assemble(ProductDTO dto) {
        return EntityModel.of(dto)
                .add(linkTo(methodOn(ProductController.class).getByName(dto.getName())).withSelfRel())
                .add(linkTo(methodOn(ProductCategoryController.class).getByName(dto.getCategoryName()))
                        .withRel("category"));
    }
}
