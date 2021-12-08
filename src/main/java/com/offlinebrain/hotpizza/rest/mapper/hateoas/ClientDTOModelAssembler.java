package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.ClientUserController;
import com.offlinebrain.hotpizza.rest.model.client.ClientModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClientDTOModelAssembler {
    public ClientModel assemble(ClientModel dto) {
        return dto.add(linkTo(methodOn(ClientUserController.class).getById(dto.getUuid())).withSelfRel())
                .add(linkTo(methodOn(ClientUserController.class).getByPhone(dto.getPhone())).withSelfRel());
    }
}
