package com.offlinebrain.hotpizza.service;

import com.offlinebrain.hotpizza.data.model.ClientUser;
import com.offlinebrain.hotpizza.data.repository.ClientRepository;
import com.offlinebrain.hotpizza.exception.ResourceExistsException;
import com.offlinebrain.hotpizza.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientUserService {
    private final ClientRepository clientRepository;

    public ClientUser create(ClientUser client) {
        clientRepository.findByPhone(client.getPhone())
                .ifPresent(clientUser -> {
                    throw new ResourceExistsException("ClientUser", "phone", clientUser.getPhone());
                });

        return clientRepository.saveAndFlush(client);
    }

    public ClientUser getByPhone(String phone) {
        return clientRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceExistsException("ClientUser", "phone", phone));
    }

    public ClientUser getByUUID(UUID uuid) {
        return clientRepository.findById(uuid)
                .orElseThrow(() -> new ResourceExistsException("ClientUser", "ID", uuid.toString()));
    }

    public boolean delete(UUID uuid) {
        boolean exists = clientRepository.existsById(uuid);
        if (!exists) {
            throw new ResourceNotFoundException("ProductCategory", "ID", uuid.toString());
        }
        clientRepository.deleteById(uuid);
        return !clientRepository.existsById(uuid);
    }
}
