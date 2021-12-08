package com.offlinebrain.hotpizza.rest.mapper.entity;

import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.rest.model.client.ClientModel;
import com.offlinebrain.hotpizza.rest.model.client.CreateClientDTO;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public ClientUser createClientDtoToClient(CreateClientDTO dto) {
        return ClientUser.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .build();
    }

    public ClientModel clientToClientDto(ClientUser entity) {
        return ClientModel.builder()
                .uuid(entity.getUuid())
                .name(entity.getName())
                .phone(entity.getPhone())
                .build();
    }
}
