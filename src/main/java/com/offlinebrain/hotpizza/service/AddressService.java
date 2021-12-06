package com.offlinebrain.hotpizza.service;

import com.offlinebrain.hotpizza.data.model.Address;
import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.data.repository.AddressRepository;
import com.offlinebrain.hotpizza.data.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;

    public Address getByUUID(UUID uuid) {
        return addressRepository.findById(uuid)
                .orElseThrow(RuntimeException::new);
    }

    public List<Address> getByClientUUID(UUID uuid) {
        return clientRepository.findById(uuid).map(ClientUser::getAddresses).orElse(new ArrayList<>());
    }

    public Address addAddress(Address address) {
        return addressRepository.saveAndFlush(address);
    }

    public boolean deleteAddress(UUID uuid) {
        boolean existsById = addressRepository.existsById(uuid);
        addressRepository.deleteById(uuid);
        return existsById && !addressRepository.existsById(uuid);
    }
}
