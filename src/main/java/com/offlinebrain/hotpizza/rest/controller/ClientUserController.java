package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.rest.mapper.entity.ClientMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.ClientDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.client.ClientModel;
import com.offlinebrain.hotpizza.rest.model.client.CreateClientDTO;
import com.offlinebrain.hotpizza.rest.validation.PhoneNumber;
import com.offlinebrain.hotpizza.service.ClientUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/clients")
public class ClientUserController {
    private final ClientUserService clientUserService;
    private final ClientMapper clientMapper;
    private final ClientDTOModelAssembler modelAssembler;

    @GetMapping(value = "/{uuid}", produces = "application/hal+json")
    public ClientModel getById(@PathVariable @NotNull UUID uuid) {
        ClientUser clientUser = clientUserService.getByUUID(uuid);

        return modelAssembler.assemble(clientMapper.clientToClientDto(clientUser));
    }

    @GetMapping(value = "/phone/{phone}", produces = "application/hal+json")
    public ClientModel getByPhone(@PathVariable @PhoneNumber String phone) {
        ClientUser clientUser = clientUserService.getByPhone(phone);

        return modelAssembler.assemble(clientMapper.clientToClientDto(clientUser));
    }

    @PostMapping(consumes = "application/json", produces = "application/hal+json")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientModel create(@RequestBody @Valid CreateClientDTO dto) {
        ClientUser clientUser = clientUserService.create(clientMapper.createClientDtoToClient(dto));

        return modelAssembler.assemble(clientMapper.clientToClientDto(clientUser));
    }

    @DeleteMapping(value = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull UUID uuid) {
        clientUserService.delete(uuid);
    }
}
