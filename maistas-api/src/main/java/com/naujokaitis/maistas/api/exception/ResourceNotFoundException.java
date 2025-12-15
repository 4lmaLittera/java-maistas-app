package com.naujokaitis.maistas.api.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String resourceType, Object id) {
        super("Could not find " + resourceType + " with id: " + id);
    }
}
