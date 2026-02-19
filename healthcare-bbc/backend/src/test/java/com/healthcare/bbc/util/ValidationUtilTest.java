package com.healthcare.bbc.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void testIsValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("13800138000"));
        assertTrue(ValidationUtil.isValidPhone("15912345678"));
        assertFalse(ValidationUtil.isValidPhone("12800138000"));
        assertFalse(ValidationUtil.isValidPhone("1380013800"));
        assertFalse(ValidationUtil.isValidPhone("138001380001"));
        assertFalse(ValidationUtil.isValidPhone(""));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    void testIsValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.uk"));
        assertFalse(ValidationUtil.isValidEmail("test@example"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail("test@.com"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testIsValidIdCard() {
        assertTrue(ValidationUtil.isValidIdCard("110101199001011234"));
        assertTrue(ValidationUtil.isValidIdCard("11010119900101123X"));
        assertTrue(ValidationUtil.isValidIdCard("110101900101123"));
        assertFalse(ValidationUtil.isValidIdCard("11010119900101123"));
        assertFalse(ValidationUtil.isValidIdCard("1101011990010112345"));
        assertFalse(ValidationUtil.isValidIdCard(""));
        assertFalse(ValidationUtil.isValidIdCard(null));
    }

    @Test
    void testIsEmpty() {
        assertTrue(ValidationUtil.isEmpty(""));
        assertTrue(ValidationUtil.isEmpty("   "));
        assertTrue(ValidationUtil.isEmpty(null));
        assertFalse(ValidationUtil.isEmpty("test"));
        assertFalse(ValidationUtil.isEmpty(" test "));
    }
}
