package org.vwf.file_handling.upload.utility;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.exceptions.EncodedDataEmptyException;
import org.vwf.file_handling.upload.exceptions.FileNotFoundException;
import org.vwf.file_handling.upload.exceptions.ImageAlreadyExistsException;
import org.vwf.file_handling.upload.exceptions.ImageNotFoundException;
import org.vwf.file_handling.upload.exceptions.InvalidContentTypeException;
import org.vwf.file_handling.upload.exceptions.InvalidFormatTypeException;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.exceptions.UserUpdateException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    /*
    -------------------- user related exceptions ------------------------
    -------------------- user related exceptions ------------------------
    */
    @ExceptionHandler(UserNotFoundException.class)
    public GenericResponse<Object> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("Inside handleUserNotFoundException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.USER_FETCH_FAILED.getMessage(),
                e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UserUpdateException.class)
    public GenericResponse<Object> handleUserUpdateException(UserUpdateException e) {
        log.warn("Inside handleUserUpdateException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.USER_UPDATE_FAILED.getMessage(),
                e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    /*
    -------------------- image/file doc related exceptions ------------------------
    -------------------- image/file doc related exceptions ------------------------
    */
    @ExceptionHandler(FileNotFoundException.class)
    public GenericResponse<Object> handleFileNotFoundException(FileNotFoundException e) {
        log.warn("Inside handleFileNotFoundException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.FILE_SAVE_FAILED.getMessage(),
                e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public GenericResponse<Object> handleInvalidContentTypeException(InvalidContentTypeException e) {
        log.warn("Inside handleInvalidContentTypeException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.FILE_SAVE_FAILED.getMessage(),
                e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(InvalidFormatTypeException.class)
    public GenericResponse<Object> handleInvalidFormatTypeException(InvalidFormatTypeException e) {
        log.warn("Inside handleInvalidFormatTypeException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.FILE_SAVE_FAILED.getMessage(),
                e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public GenericResponse<Object> handleImageNotFoundException(ImageNotFoundException e) {
        log.warn("Inside handleImageNotFoundException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.IMAGE_FETCH_FAILED.getMessage(),
                e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(ImageAlreadyExistsException.class)
    public GenericResponse<Object> handleImageAlreadyExistsException(ImageAlreadyExistsException e) {
        log.warn("Inside handleImageAlreadyExistsException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.IMAGE_EXIST_BY_SAME_DATA.getMessage(),
                e.getMessage(), HttpStatus.FOUND.value());
    }

    /*
    -------------------- generic exceptions ------------------------
    -------------------- generic exceptions ------------------------
    */
    // response will be given in below-mentioned format
    // { "status":0, "message":"<errRespMessage>", "errors": map/String }

    @ExceptionHandler(IOException.class)
    public GenericResponse<java.lang.Object> handleIOException(IOException e) {
        log.error("Inside handleIOException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.IO_EXCEPTION_FAILURE.getMessage(),
                e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public GenericResponse<Object> handleNoSuchAlgorithmException(NoSuchAlgorithmException e) {
        log.error("Inside handleNoSuchAlgorithmException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.NO_SUCH_ALGORITHM_ERROR.getMessage(),
                e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(EncodedDataEmptyException.class)
    public GenericResponse<Object> handleEncodedDataEmptyException(EncodedDataEmptyException e) {
        log.warn("Inside handleEncodedDataEmptyException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.DOC_COMPARE_FAILED.getMessage(),
                e.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public GenericResponse<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Inside handleIllegalArgumentException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.INVALID_ARGS_PASSED.getMessage(),
                e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    public GenericResponse<Object> handleRuntimeException(Exception e) {
        log.error("Inside handleRuntimeException Handler: ", e);
        return buildExceptionResponse(ResponseMessage.INTERNAL_SERVER_ERROR.getMessage(),
                e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Inside handleMethodArgumentNotValidException Handler: ", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getAllErrors().forEach(error -> {
            // to set fieldName as key and errorMessage from validations as value
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return buildInternalErrorsResponse(ResponseMessage.REQUEST_VALIDATIONS_FAILED.getMessage(),
                errors, HttpStatus.BAD_REQUEST);
    }

    private GenericResponse<Object> buildExceptionResponse(String message, Object errors, int statusCode) {
        return GenericResponse.failure(message, statusCode, errors);
    }

    private ResponseEntity<Object> buildInternalErrorsResponse(String message, Object errors, HttpStatus status) {
        return ResponseEntity.status(status).body(buildExceptionResponse(message,errors, status.value()));
    }
}
