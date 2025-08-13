package org.vwf.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class InvalidContentTypeException extends RuntimeException{

    public InvalidContentTypeException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
