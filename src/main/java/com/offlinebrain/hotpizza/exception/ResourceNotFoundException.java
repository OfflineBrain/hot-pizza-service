package com.offlinebrain.hotpizza.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final String identifierName;
    private final String identifier;


    public ResourceNotFoundException(String resource, String identifierName, String identifier) {
        super("%s not found by [%s:%s]".formatted(resource, identifierName, identifier));
        this.resource = resource;
        this.identifierName = identifierName;
        this.identifier = identifier;
    }
}
