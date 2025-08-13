package com.wecodee.file_handling.upload.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {
    USER_NOT_FOUND("User was not found by ID in the database"),
    USER_DELETE_FAIL("User record deletion failed, since user was not found"),
    USER_UPDATE_FAIL("User update failed, since user was not found"),
    USER_NOT_FOUND_INREQUEST("User Details absent in request"),
    USERID_NOT_FOUND_INREQUEST("User ID not found in request"),

    IMAGE_SAVE_FAIL("Image save has failed, Please check logs"),
    DOC_INVALID_TYPE("Document Content Type is invalid"),
    DOC_INVALID_FORMAT_TYPE("Document Format type is Invalid"),
    FILE_UPLOAD_FAILED("File not uploaded properly, contains empty data"),
    IMAGE_NOT_FOUND("Image was not found by ID in the database"),
    ENCODED_DATA_EMPTY_ERROR("Encoded Data is blank/empty among the two"),
    IMAGE_ALREADY_EXISTS("Image already exists by same data content"),
    FILE_NOT_FOUND("File was not found by ID in the database"),
    FILE_REQUEST_DATA_EMPTY("File Request data is empty, please check logs");

    private final String message;
}
