package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.AddressController;
import com.offlinebrain.hotpizza.rest.model.address.AddressModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AddressDTOModelAssembler {
    public AddressModel assemble(AddressModel dto) {
        return dto.add(linkTo(methodOn(AddressController.class).getByUUID(dto.getUuid())).withSelfRel());
    }

    public CollectionModel<AddressModel> assemble(List<AddressModel> dtos) {
        return CollectionModel.of(dtos.stream()
                .map(this::assemble).toList());
    }
}
