package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
