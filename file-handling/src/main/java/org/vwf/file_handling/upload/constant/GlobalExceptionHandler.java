package org.vwf.file_handling.upload.constant;

import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vwf.file_handling.upload.exceptions.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // -------------------- user related exceptions ------------------------
    @ExceptionHandler(UserSaveException.class)
    public ApiResponse<JSONObject> handleUserSaveException(UserSaveException e){
        log.warn("Inside handleUserSaveException Handler");
        return ApiResponse.failure(ResponseMessage.USER_SAVE_FAILED.getMessage(), ErrorCodes.FH_USER_SAVE_FAIL.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<JSONObject> handleUserNotFoundException(UserNotFoundException e){
        log.warn("Inside handleUserNotFoundException Handler");
        return ApiResponse.failure(ResponseMessage.USER_FETCH_FAILED.getMessage(), ErrorCodes.FH_USER_FETCH_FAIL.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(UserUpdateException.class)
    public ApiResponse<JSONObject> handleUserUpdateException(UserUpdateException e){
        log.warn("Inside handleUserUpdateException Handler");
        return ApiResponse.failure(ResponseMessage.USER_UPDATE_FAILED.getMessage(), ErrorCodes.FH_USER_UPDATE_FAIL.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(UserDeleteException.class)
    public ApiResponse<JSONObject> handleUserDeleteException(UserDeleteException e){
        log.warn("Inside handleUserDeleteException Handler");
        return ApiResponse.failure(ResponseMessage.USER_DELETE_FAILED.getMessage(), ErrorCodes.FH_USER_DELETE_FAIL.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    // -------------------- image/file doc related exceptions ------------------------
    @ExceptionHandler(FileNotFoundException.class)
    public ApiResponse<JSONObject> handleFileNotFoundException(FileNotFoundException e) {
        log.warn("Inside handleFileNotFoundException Handler");
        return ApiResponse.failure(ResponseMessage.FILE_SAVE_FAILED.getMessage(), ErrorCodes.FH_IMAGE_SAVE_FAIL.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ApiResponse<JSONObject> handleInvalidContentTypeException(InvalidContentTypeException e) {
        log.warn("Inside handleInvalidContentTypeException Handler");
        return ApiResponse.failure(ResponseMessage.FILE_SAVE_FAILED.getMessage(), ErrorCodes.FH_IMG_CONTENTTYPE_INVALID.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(InvalidFormatTypeException.class)
    public ApiResponse<JSONObject> handleInvalidFormatTypeException(InvalidFormatTypeException e) {
        log.warn("Inside handleInvalidFormatTypeException Handler");
        return ApiResponse.failure(ResponseMessage.FILE_SAVE_FAILED.getMessage(), ErrorCodes.FH_IMG_FORMAT_INVALID.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ApiResponse<JSONObject> handleImageNotFoundException(ImageNotFoundException e) {
        log.warn("Inside handleImageNotFoundException Handler");
        return ApiResponse.failure(ResponseMessage.IMAGE_FETCH_FAILED.getMessage(), ErrorCodes.FH_IMG_NOT_FOUND.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(ImageAlreadyExistsException.class)
    public ApiResponse<JSONObject> handleImageAlreadyExistsException(ImageAlreadyExistsException e){
        log.warn("Inside handleImageAlreadyExistsException Handler");
        return ApiResponse.failure(ResponseMessage.IMAGE_EXIST_BY_SAME_DATA.getMessage(), ErrorCodes.FH_IMG_UPLOAD_FAIL.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    // -------------------- generic exceptions ------------------------
    // response will be given in below-mentioned format
    // { "status":0, "message":"<respMessage>", "data": { "exceptionMessage": "<eMessage>" } }
    @ExceptionHandler(IOException.class)
    public ApiResponse<JSONObject> handleIOException(IOException e) {
        log.error("Inside handleIOException Handler", e);
        return ApiResponse.failure(ResponseMessage.IO_EXCEPTION_FAILURE.getMessage(), ErrorCodes.FH_IO_EXCEPTION.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ApiResponse<JSONObject> handleNoSuchAlgorithmException(NoSuchAlgorithmException e) {
        log.error("Inside handleNoSuchAlgorithmException Handler", e);
        return ApiResponse.failure(ResponseMessage.NO_SUCH_ALGORITHM_ERROR.getMessage(), ErrorCodes.FH_NO_SUCH_ALGO.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(EncodedDataEmptyException.class)
    public ApiResponse<JSONObject> handleEncodedDataEmptyException(EncodedDataEmptyException e) {
        log.warn("Inside handleEncodedDataEmptyException Handler");
        return ApiResponse.failure(ResponseMessage.DOC_COMPARE_FAILED.getMessage(), ErrorCodes.FH_ENCODED_DATA_EMPTY.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<JSONObject> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Inside handleIllegalArgumentException Handler", e);
        return ApiResponse.failure(ResponseMessage.INVALID_ARGS_PASSED.getMessage(), ErrorCodes.FH_INVALID_ARGS.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<JSONObject> handleRuntimeException(RuntimeException e){
        log.error("Inside handleRuntimeException Handler", e);
        return ApiResponse.failure(ResponseMessage.INTERNAL_SERVER_ERROR.getMessage(), ErrorCodes.FH_INTERNAL_SERVER_ERROR.getErrorCode(),
                new JSONObject(Map.of("exceptionMessage", e.getMessage())));
    }


}
