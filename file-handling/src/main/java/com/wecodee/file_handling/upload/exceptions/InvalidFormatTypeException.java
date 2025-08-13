package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class InvalidFormatTypeException extends RuntimeException {

    public InvalidFormatTypeException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
