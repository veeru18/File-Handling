package org.vwf.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class PasswordValidationFailException extends RuntimeException {

    public PasswordValidationFailException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
