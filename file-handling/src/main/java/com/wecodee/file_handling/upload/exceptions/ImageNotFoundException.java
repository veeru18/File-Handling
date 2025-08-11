package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
