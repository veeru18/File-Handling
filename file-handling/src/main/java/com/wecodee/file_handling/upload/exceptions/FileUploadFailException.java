package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class FileUploadFailException extends RuntimeException{

    public FileUploadFailException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
