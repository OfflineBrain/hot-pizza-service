package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.rest.mapper.entity.ClientMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.ClientDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.client.ClientDTO;
import com.offlinebrain.hotpizza.rest.model.client.CreateClientDTO;
import com.offlinebrain.hotpizza.rest.validation.PhoneNumber;
import com.offlinebrain.hotpizza.service.ClientUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/clients")
public class ClientUserController {
    private final ClientUserService clientUserService;
    private final ClientMapper clientMapper;
    private final ClientDTOModelAssembler modelAssembler;

    @GetMapping(value = "/{uuid}", produces = "application/hal+json")
    public ResponseEntity<EntityModel<ClientDTO>> getById(@PathVariable @NotNull UUID uuid) {
        ClientUser clientUser = clientUserService.getByUUID(uuid);
        EntityModel<ClientDTO> model = modelAssembler.assemble(clientMapper.clientToClientDto(clientUser));

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/phone/{phone}", produces = "application/hal+json")
    public ResponseEntity<EntityModel<ClientDTO>> getByPhone(@PathVariable @PhoneNumber String phone) {
        ClientUser clientUser = clientUserService.getByPhone(phone);
        EntityModel<ClientDTO> model = modelAssembler.assemble(clientMapper.clientToClientDto(clientUser));

        return ResponseEntity.ok(model);
    }

    @PostMapping(consumes = "application/json", produces = "application/hal+json")
    public ResponseEntity<EntityModel<ClientDTO>> create(@RequestBody @Valid CreateClientDTO dto) {
        ClientUser clientUser = clientUserService.create(clientMapper.createClientDtoToClient(dto));
        EntityModel<ClientDTO> model = modelAssembler.assemble(clientMapper.clientToClientDto(clientUser));

        return ResponseEntity.created(
                        linkTo(methodOn(ClientUserController.class).getById(clientUser.getUuid())).toUri())
                .body(model);
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<Object> delete(@PathVariable @NotNull UUID uuid) {
        boolean deleted = clientUserService.delete(uuid);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
