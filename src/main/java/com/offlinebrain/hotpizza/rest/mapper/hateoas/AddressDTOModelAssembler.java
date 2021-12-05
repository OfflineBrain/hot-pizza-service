package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.AddressController;
import com.offlinebrain.hotpizza.rest.model.address.AddressDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AddressDTOModelAssembler {
    public EntityModel<AddressDTO> assemble(AddressDTO dto) {
        return EntityModel.of(dto)
                .add(linkTo(methodOn(AddressController.class).getByUUID(dto.getUuid())).withSelfRel());
    }

    public CollectionModel<EntityModel<AddressDTO>> assemble(List<AddressDTO> dtos) {
        return CollectionModel.of(dtos.stream()
                .map(this::assemble).toList());
    }
}
