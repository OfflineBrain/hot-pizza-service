package com.offlinebrain.hotpizza.exception;

import lombok.Getter;

@Getter
public class ResourceExistsException extends RuntimeException {
    private final String resource;
    private final String identifierName;
    private final String identifier;


    public ResourceExistsException(String resource, String identifierName, String identifier) {
        super("%s already exists [%s:%s]".formatted(resource, identifierName, identifier));
        this.resource = resource;
        this.identifierName = identifierName;
        this.identifier = identifier;
    }
}
