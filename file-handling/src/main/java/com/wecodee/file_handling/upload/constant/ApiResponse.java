package com.wecodee.file_handling.upload.constant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;

    public ApiResponse(int status, String message, T data) {
        super();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int status, String message, T data, String errorCode) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static <T> ApiResponse<T> success(String message) {
        return new <T> ApiResponse<T>(1, message, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new <T> ApiResponse<T>(1, message, data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new <T> ApiResponse<T>(0, message, null);
    }

    public static <T> ApiResponse<T> failure(String message, String errorCode) {
        return new <T> ApiResponse<T>(0, message, null, errorCode);
    }

    public static <T> ApiResponse<T> failure(String message, String errorCode, T data) {
        return new <T> ApiResponse<T>(0, message, data, errorCode);
    }

    public static <T> ApiResponse<T> failure(String message, T data) {
        return new <T> ApiResponse<T>(0, message, data);
    }

    public static <T> ApiResponse<T> warning(String message) {
        return new <T> ApiResponse<T>(2, message, null);
    }

    public static <T> ApiResponse<T> warning(String message, T data) {
        return new <T> ApiResponse<T>(2, message, data);
    }

    public static <T> ApiResponse<T> limitReached(String message) {
        return new <T> ApiResponse<T>(-1, message, null);
    }

    public static <T> ApiResponse<T> customerApplicationIsInProgressAlready(String message, T data) {
        return new <T> ApiResponse<T>(-1, message, data);
    }

    public static <T> ApiResponse<T> customerOnboardingApplicationRequestIsSubmittedAlready(String message) {
        return new <T> ApiResponse<T>(-1, message, null);
    }

}
