package com.wecodee.file_handling.upload.exceptions;

import lombok.Getter;

@Getter
public class UserSaveException extends RuntimeException{

    public UserSaveException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
