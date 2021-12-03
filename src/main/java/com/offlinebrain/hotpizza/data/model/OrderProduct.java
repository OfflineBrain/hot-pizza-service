package com.offlinebrain.hotpizza.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_product")
@IdClass(OrderProduct.Key.class)
public class OrderProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "client_order")
    private ClientOrder clientOrder;
    @Id
    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;
    private Integer quantity;

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Key implements Serializable {
        private ClientOrder clientOrder;
        private Product product;
    }
}
