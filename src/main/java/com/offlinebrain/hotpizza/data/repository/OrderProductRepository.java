package com.offlinebrain.hotpizza.data.repository;

import com.offlinebrain.hotpizza.data.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProduct.Key> {
}
