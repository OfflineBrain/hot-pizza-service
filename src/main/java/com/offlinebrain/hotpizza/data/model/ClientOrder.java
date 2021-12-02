package com.offlinebrain.hotpizza.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "client_order")
public class ClientOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;
    @ManyToOne
    @JoinColumn(name = "client")
    private ClientUser client;
    @Enumerated(EnumType.STRING)
    private OrderState state;
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long number;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdatedDate;
    private String clientComment;
    private String serviceComment;
}
