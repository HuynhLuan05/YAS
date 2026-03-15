package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void errorCode_constants_areDefined() {
        assertNotNull(Constants.ErrorCode.COUNTRY_NOT_FOUND);
        assertNotNull(Constants.ErrorCode.NAME_ALREADY_EXITED);
        assertNotNull(Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND);
        assertNotNull(Constants.ErrorCode.ADDRESS_NOT_FOUND);
        assertNotNull(Constants.ErrorCode.CODE_ALREADY_EXISTED);
    }

    @Test
    void pageableConstant_constants_areDefined() {
        assertNotNull(Constants.PageableConstant.DEFAULT_PAGE_SIZE);
        assertNotNull(Constants.PageableConstant.DEFAULT_PAGE_NUMBER);
    }

    @Test
    void apiConstant_constants_areDefined() {
        assertNotNull(Constants.ApiConstant.COUNTRIES_URL);
        assertNotNull(Constants.ApiConstant.STATE_OR_PROVINCES_URL);
        assertNotNull(Constants.ApiConstant.CODE_200);
        assertNotNull(Constants.ApiConstant.OK);
    }
}
