package com.offlinebrain.hotpizza.rest.mapper.hateoas;

import com.offlinebrain.hotpizza.rest.controller.ClientUserController;
import com.offlinebrain.hotpizza.rest.model.client.ClientDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClientDTOModelAssembler {
    public EntityModel<ClientDTO> assemble(ClientDTO dto) {
        return EntityModel.of(dto)
                .add(linkTo(methodOn(ClientUserController.class).getById(dto.getUuid())).withSelfRel())
                .add(linkTo(methodOn(ClientUserController.class).getByPhone(dto.getPhone())).withSelfRel());
    }
}
