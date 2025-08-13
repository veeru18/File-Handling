package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
