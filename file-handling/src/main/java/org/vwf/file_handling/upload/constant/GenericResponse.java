package org.vwf.file_handling.upload.constant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GenericResponse<T> {

    private Boolean success; // boolean or "status" as string
    private String message; // response message
    private T data; // or "payload"
    private Object errors; // for exceptions ( both custom and internal )
    private Integer statusCode; // http status

    GenericResponse(boolean success, String message, Integer statusCode) {
        this.success = success;
        this.message = message;
        this.statusCode = statusCode;
    }

    // success response constructor
    public GenericResponse(boolean success, String message, T data) {
        this(success, message, 200); // ok status as default value
        this.data = data;
    }

    // for custom exception handler
    public GenericResponse(boolean success, String message, Object errors, Integer statusCode) {
        this(success, message, statusCode);
        this.errors = errors;
    }

    public static <T> GenericResponse<T> success(String message, T data) {
        return new GenericResponse<>(true, message, data);
    }

    public static <T> GenericResponse<T> failure(String message, Integer statusCode, Object errors) {
        return new <T>GenericResponse<T>(false, message, errors, statusCode);
    }

}
