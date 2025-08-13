package org.vwf.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
