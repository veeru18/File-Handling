package org.vwf.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class UserUpdateException extends RuntimeException{

    public UserUpdateException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
