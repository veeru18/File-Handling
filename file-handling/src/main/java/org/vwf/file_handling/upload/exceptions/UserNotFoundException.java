package org.vwf.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
