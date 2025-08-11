package com.wecodee.file_handling.upload.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCodes {

    FH_INVALID_ARGS("FH0001"),
    //for users
    FH_USER_SAVE_FAIL("FH1001"),
    FH_USER_FETCH_FAIL("FH1002"),
    FH_USER_UPDATE_FAIL("FH1003"),
    FH_USER_DELETE_FAIL("FH1004"),

    FH_IMAGE_SAVE_FAIL("FH2001"),
    FH_IMG_CONTENTTYPE_INVALID("FH2002"),
    FH_IMG_FORMAT_INVALID("FH2003");

    private final String errorCode;
}
