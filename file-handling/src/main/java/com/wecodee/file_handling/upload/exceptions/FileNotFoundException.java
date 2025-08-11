package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class FileNotFoundException extends RuntimeException{

    public FileNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
