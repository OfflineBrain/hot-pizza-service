package com.offlinebrain.hotpizza.rest.model.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressModel extends RepresentationModel<AddressModel> {
    private UUID uuid;
    private UUID clientUser;
    private String address;
}
