package org.vwf.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class EncodedDataEmptyException extends RuntimeException {

    public EncodedDataEmptyException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
