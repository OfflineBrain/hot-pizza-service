package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.data.model.Address;
import com.offlinebrain.hotpizza.rest.mapper.entity.AddressMapper;
import com.offlinebrain.hotpizza.rest.mapper.hateoas.AddressDTOModelAssembler;
import com.offlinebrain.hotpizza.rest.model.address.AddressDTO;
import com.offlinebrain.hotpizza.rest.model.address.CreateAddressDTO;
import com.offlinebrain.hotpizza.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final AddressDTOModelAssembler assembler;

    @Cacheable(value = "addresses", key = "uuid")
    @GetMapping(value = "/clients/{uuid}/addresses", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CollectionModel<EntityModel<AddressDTO>>> getAllByUser(@PathVariable @NotNull UUID uuid) {
        List<AddressDTO> addresses = addressService.getByClientUUID(uuid)
                .stream().map(addressMapper::addressToAddressDto).toList();
        return ResponseEntity.ok(assembler.assemble(addresses));
    }

    @Cacheable(value = "address", key = "uuid")
    @GetMapping(value = "/addresses/{uuid}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<EntityModel<AddressDTO>> getByUUID(@PathVariable @NotNull UUID uuid) {
        AddressDTO address = addressMapper.addressToAddressDto(addressService.getByUUID(uuid));
        return ResponseEntity.ok(assembler.assemble(address));
    }

    @Caching(evict = {@CacheEvict(value = "addresses")},
            put = {@CachePut(value = "address", key = "#result.body.content.uuid")})
    @GetMapping(value = "/addresses", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<EntityModel<AddressDTO>> addAddress(@RequestBody CreateAddressDTO dto) {
        Address address = addressMapper.createAddressDtoToAddress(dto);
        AddressDTO savedAddress = addressMapper.addressToAddressDto(addressService.addAddress(address));
        return ResponseEntity.created(
                        linkTo(methodOn(AddressController.class).getByUUID(savedAddress.getUuid())).toUri())
                .body(assembler.assemble(savedAddress));
    }

    @Caching(evict = {@CacheEvict(value = "addresses"),
            @CacheEvict(value = "address", key = "uuid")})
    @DeleteMapping("/addresses/{uuid}")
    @ResponseBody
    public ResponseEntity<Object> removeAddress(@PathVariable @NotNull UUID uuid) {
        boolean deleted = addressService.deleteAddress(uuid);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
