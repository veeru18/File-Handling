package com.wecodee.file_handling.upload.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCodes {

    FH_INVALID_ARGS("FH0001"),
    FH_NO_SUCH_ALGO("FH0002"),
    FH_IO_EXCEPTION("FH0003"),
    FH_ENCODED_DATA_EMPTY("FH0004"),
    FH_INTERNAL_SERVER_ERROR("FH5000"),
    //for users
    FH_USER_SAVE_FAIL("FH1001"),
    FH_USER_FETCH_FAIL("FH1002"),
    FH_USER_UPDATE_FAIL("FH1003"),
    FH_USER_DELETE_FAIL("FH1004"),

    FH_IMAGE_SAVE_FAIL("FH2001"),
    FH_IMG_CONTENTTYPE_INVALID("FH2002"),
    FH_IMG_FORMAT_INVALID("FH2003"),
    FH_IMG_NOT_FOUND("FH2004"),
    FH_IMG_UPLOAD_FAIL("FH2005");

    private final String errorCode;
}
