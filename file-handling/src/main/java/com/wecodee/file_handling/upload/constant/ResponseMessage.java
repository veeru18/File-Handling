package com.wecodee.file_handling.upload.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    //fetch response
    IMAGE_FETCH_SUCCESS("Image fetched Successfully"),
    DOCUMENT_FETCH_SUCCESS("Document fetched Successfully"),
    USER_FETCH_SUCCESS("User fetched Successfully"),
    USER_FETCH_FAILED("User fetch failed"),
    IMAGE_FETCH_FAILED("Image fetch failed"),

    //save responses
    USER_SAVE_FAILED("User Save failed"),
    USER_SAVE_SUCCESS("User Save success"),
    DOCUMENT_SAVE_FAILED("Document Save failed"),
    DOCUMENT_SAVE_SUCCESS("Document Save success"),
    IMAGE_SAVE_FAILED("Image Save failed"),
    IMAGE_SAVE_SUCCESS("Image Save success"),

    //update responses
    USER_UPDATE_FAILED("User Update Failed"),
    IMAGE_UPDATE_FAILED("Image update Failed"),
    DOCUMENT_UPDATE_FAILED("Document update Failed"),

    //delete responses
    USER_DELETE_FAILED("User delete Failed"),

    INVALID_ARGS_PASSED("Invalid Arguments passed"),
    IO_EXCEPTION_FAILURE("IO exception has occurred"),
    NO_SUCH_ALGORITHM_ERROR("General Security Error, no algorithm could be found for specified string"),
    DOC_COMPARE_FAILED("Documents comparison has failed, mostly due to empty/invalid byte data"),
    INTERNAL_SERVER_ERROR("Internal server error, runtime exception occurred, please check logs!");

    private final String message;

}
