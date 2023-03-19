package com.example.userservice.exception;

public class ResourceAlreadyExistException extends RuntimeException{
    public ResourceAlreadyExistException(String msg) {
        super(msg);
    }

    public ResourceAlreadyExistException(String resource, String key, Object value) {
        super(String.format("%s with %s '%s' already exist", resource, key, value));
    }
}

