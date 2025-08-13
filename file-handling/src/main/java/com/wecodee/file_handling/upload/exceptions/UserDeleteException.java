package com.wecodee.file_handling.upload.exceptions;

public class UserDeleteException extends RuntimeException{

    public UserDeleteException(String message) {
        super(message);
        this.message = message;
    }

    private final String message;
}
