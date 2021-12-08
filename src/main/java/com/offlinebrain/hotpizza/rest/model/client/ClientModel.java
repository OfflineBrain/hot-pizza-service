package com.offlinebrain.hotpizza.rest.model.client;

import com.offlinebrain.hotpizza.rest.validation.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClientModel extends RepresentationModel<ClientModel> {
    private UUID uuid;
    private String name;
    @PhoneNumber
    private String phone;
}
