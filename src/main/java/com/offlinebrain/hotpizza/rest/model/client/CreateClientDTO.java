package com.offlinebrain.hotpizza.rest.model.client;

import com.offlinebrain.hotpizza.rest.validation.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateClientDTO {
    @NotEmpty
    private String name;
    @PhoneNumber
    @NotNull
    private String phone;
}
