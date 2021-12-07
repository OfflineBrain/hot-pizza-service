package com.offlinebrain.hotpizza.rest.model.client;

import com.offlinebrain.hotpizza.rest.validation.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClientDTO {
    private UUID uuid;
    private String name;
    @PhoneNumber
    private String phone;
}
