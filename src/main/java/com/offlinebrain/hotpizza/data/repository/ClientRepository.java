package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientUser, UUID> {
    Optional<ClientUser> findByPhone(String phone);
}
