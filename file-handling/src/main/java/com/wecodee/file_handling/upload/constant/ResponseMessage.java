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

    //save responses
    USER_SAVE_FAILED("User Save failed"),
    USER_SAVE_SUCCESS("User Save success"),
    DOCUMENT_SAVE_FAILED("Document Save failed"),
    DOCUMENT_SAVE_SUCCESS("Document Save success"),
    IMAGE_SAVE_FAILED("Image Save failed"),
    IMAGE_SAVE_SUCCESS("Image Save success")

    ;

    private final String message;

}
