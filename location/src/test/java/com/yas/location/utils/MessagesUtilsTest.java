package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_withValidCode_returnsFormattedMessage() {
        String result = MessagesUtils.getMessage("COUNTRY_NOT_FOUND", "VN");
        assertEquals("The country VN is not found", result);
    }

    @Test
    void getMessage_withInvalidCode_returnsCodeAsMessage() {
        String result = MessagesUtils.getMessage("invalid.code.here");
        assertEquals("invalid.code.here", result);
    }

    @Test
    void getMessage_withPlaceholder_replacesCorrectly() {
        String result = MessagesUtils.getMessage("ADDRESS_NOT_FOUND", 123L);
        assertEquals("The address 123 is not found", result);
    }
}
