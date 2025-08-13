package org.vwf.file_handling.upload.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    //fetch response
    IMAGE_FETCH_SUCCESS("Image fetched successfully"),
    DOCUMENT_FETCH_SUCCESS("Document fetched successfully"),
    USER_FETCH_SUCCESS("User fetched successfully"),
    USER_FETCH_FAILED("User fetch failed"),
    IMAGE_FETCH_FAILED("Image fetch failed"),

    //save responses
    USER_SAVE_FAILED("User Save failed"),
    USER_SAVE_SUCCESS("User Save successfully"),
    DOCUMENT_SAVE_FAILED("Document Save failed"),
    DOCUMENT_SAVE_SUCCESS("Document Save successfully"),
    FILE_SAVE_FAILED("File Save failed"),
    IMAGE_SAVE_SUCCESS("File Saved successfully"),

    //update responses
    USER_UPDATE_FAILED("User Update Failed"),
    USER_UPDATE_SUCCESS("User update success"),

    //delete responses
    USER_DELETE_FAILED("User delete Failed"),
    USER_DELETE_SUCCESS("User delete success"),

    INVALID_ARGS_PASSED("Invalid Arguments passed"),
    IO_EXCEPTION_FAILURE("IO exception has occurred"),
    NO_SUCH_ALGORITHM_ERROR("General Security Error, no algorithm could be found for specified string"),
    DOC_COMPARE_FAILED("Documents comparison has failed, mostly due to empty/invalid byte data"),
    INTERNAL_SERVER_ERROR("Internal server error, runtime exception occurred, please check logs!"),
    IMAGE_EXIST_BY_SAME_DATA("Image upload failed, due to same DataContent check b/w existing image and uploaded one"),

    USER_REGISTER_SUCCESS("User Registered Successfully"),
    USER_LOGIN_SUCCESS("User Logged in Successfully");

    private final String message;

}
