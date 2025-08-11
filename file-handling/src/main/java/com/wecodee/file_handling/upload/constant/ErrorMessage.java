package com.wecodee.file_handling.upload.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {
    USER_NOT_FOUND("User was not found by ID in the database"),
    USER_DELETE_FAIL("User record deletion failed, since user was not found"),
    USER_UPDATE_FAIL("User update failed, since user was not found"),

    IMAGE_SAVE_FAIL("Image save has failed, Please check logs"),
    IMAGE_INVALID_TYPE("Image Content Type is invalid"),
    IMAGE_INVALID_FORMAT_TYPE("Image Format type is Invalid"),
    FILE_UPLOAD_FAILED("File not uploaded properly, contains empty data");

    private final String message;
}
