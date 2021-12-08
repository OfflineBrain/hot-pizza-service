package com.offlinebrain.hotpizza.rest.mapper.entity;

import com.offlinebrain.hotpizza.data.model.Address;
import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.rest.model.address.AddressModel;
import com.offlinebrain.hotpizza.rest.model.address.CreateAddressDTO;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public Address createAddressDtoToAddress(CreateAddressDTO dto) {
        return Address.builder()
                .address(dto.getAddress())
                .clientUser(ClientUser.builder()
                        .uuid(dto.getClientUser()).build())
                .build();
    }

    public AddressModel addressToAddressDto(Address address) {
        return AddressModel.builder()
                .address(address.getAddress())
                .clientUser(address.getClientUser().getUuid())
                .uuid(address.getUuid())
                .build();
    }
}
