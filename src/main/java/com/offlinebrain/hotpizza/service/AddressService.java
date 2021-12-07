package com.offlinebrain.hotpizza.service;

import com.offlinebrain.hotpizza.data.model.Address;
import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.data.repository.AddressRepository;
import com.offlinebrain.hotpizza.data.repository.ClientRepository;
import com.offlinebrain.hotpizza.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", uuid.toString()));
    }

    public List<Address> getByClientUUID(UUID uuid) {
        return clientRepository.findById(uuid).map(ClientUser::getAddresses)
                .orElseThrow(() -> new ResourceNotFoundException("ClientUser", "ID", uuid.toString()));
    }

    public Address addAddress(Address address) {
        clientRepository.findById(address.getClientUser().getUuid())
                .orElseThrow(() ->
                        new ResourceNotFoundException("ClientUser", "ID",
                                address.getClientUser().getUuid().toString())
                );
        return addressRepository.saveAndFlush(address);
    }

    public boolean deleteAddress(UUID uuid) {
        boolean existsById = addressRepository.existsById(uuid);
        addressRepository.deleteById(uuid);
        return existsById && !addressRepository.existsById(uuid);
    }
}
