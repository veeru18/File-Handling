package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class InvalidRequestDataException extends RuntimeException {

    public InvalidRequestDataException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
