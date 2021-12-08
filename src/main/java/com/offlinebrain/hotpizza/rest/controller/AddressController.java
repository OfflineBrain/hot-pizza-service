package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.Address;
import com.offlinebrain.hotpizza.rest.mapper.entity.AddressMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.AddressDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.address.AddressDTO;
import com.offlinebrain.hotpizza.rest.model.address.CreateAddressDTO;
import com.offlinebrain.hotpizza.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final AddressDTOModelAssembler assembler;

    @Cacheable(value = "addresses", key = "#uuid")
    @GetMapping(value = "/clients/{uuid}/addresses", produces = "application/json")
    @ResponseBody
    public CollectionModel<EntityModel<AddressDTO>> getAllByUser(@PathVariable @NotNull UUID uuid) {
        List<AddressDTO> addresses = addressService.getByClientUUID(uuid)
                .stream().map(addressMapper::addressToAddressDto).toList();
        return assembler.assemble(addresses);
    }

    @Cacheable(value = "address", key = "#uuid")
    @GetMapping(value = "/addresses/{uuid}", produces = "application/json")
    @ResponseBody
    public EntityModel<AddressDTO> getByUUID(@PathVariable @NotNull UUID uuid) {
        AddressDTO address = addressMapper.addressToAddressDto(addressService.getByUUID(uuid));
        return assembler.assemble(address);
    }

    @Caching(evict = {@CacheEvict(value = "addresses")})
    @PostMapping(value = "/addresses", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<AddressDTO> addAddress(@RequestBody CreateAddressDTO dto) {
        Address address = addressMapper.createAddressDtoToAddress(dto);
        AddressDTO savedAddress = addressMapper.addressToAddressDto(addressService.addAddress(address));
        return assembler.assemble(savedAddress);
    }

    @Caching(evict = {@CacheEvict(value = "addresses"),
            @CacheEvict(value = "address", key = "#uuid")})
    @DeleteMapping("/addresses/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAddress(@PathVariable @NotNull UUID uuid) {
        addressService.deleteAddress(uuid);
    }
}
