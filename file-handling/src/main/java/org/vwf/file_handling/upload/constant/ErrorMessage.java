package org.vwf.file_handling.upload.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {
    USER_NOT_FOUND("User was not found by ID in the database"),
    USER_UPDATE_FAIL("User update failed, since user was not found"),
    USER_NOT_FOUND_INREQUEST("User Details absent in request"),

    IMAGE_SAVE_FAIL("Image save has failed, Please check logs"),
    DOC_INVALID_TYPE("Document Content Type is invalid"),
    DOC_INVALID_FORMAT_TYPE("Document Format type is Invalid"),
    FILE_UPLOAD_FAILED("File not uploaded properly, contains empty data"),
    IMAGE_NOT_FOUND("Image was not found by ID in the database"),
    IMAGE_NOT_FOUND_REQ("Image was not found in request body as multipart!"),
    ENCODED_DATA_EMPTY_ERROR("Encoded Data is blank/empty among the two"),
    IMAGE_ALREADY_EXISTS("Image already exists by same data content"),
    FILE_NOT_FOUND("File was not found by ID in the database"),
    FILE_REQUEST_DATA_EMPTY("File Request data is empty, please check logs"),
    REQUEST_DATA_EMPTY("Request Object's data is empty, please send valid data"),
    USER_EXISTS_ERROR("User already exists by the provided email, please try login!"),
    PASSWORD_CONTAINS_WHITESPACE("Password Contains Whitespace"),
    PWD_CHAR_COUNTS_NOT_VALID("Password must contain atleast 1 "),
    USER_NOT_FOUND_BY_EMAIL("User not found by email, Please provide valid registered email"),
    NO_USERS_EXIST("No User data exists in database"),
    EXPIRED_JWT("Expired Jwt token, please try login again"),
    MALFORMED_JWT("Invalid Jwt token form, please try logging in once"),
    JWT_FILTER_ERROR("Jwt token issue, do check logs!");

    private final String message;
}
