package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class ImageAlreadyExistsException extends RuntimeException {

    public ImageAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
