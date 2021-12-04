package com.offlinebrain.hotpizza.rest.model.product;

import com.offlinebrain.hotpizza.data.model.AmountUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDTO {
    private UUID uuid;
    private String name;
    private String description;
    private UUID categoryUUID;
    private BigDecimal price;
    private Integer amount;
    private AmountUnit amountUnit;
}
