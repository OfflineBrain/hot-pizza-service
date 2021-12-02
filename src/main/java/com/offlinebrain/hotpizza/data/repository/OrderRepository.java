package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<ClientOrder, UUID> {
}
