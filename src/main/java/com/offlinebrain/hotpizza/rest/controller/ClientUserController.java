package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.rest.mapper.entity.ClientMapper;
import com.offlinebrain.hotpizza.service.ClientUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/clients")
public class ClientUserController {
    private final ClientUserService clientUserService;
    private final ClientMapper clientMapper;
}
