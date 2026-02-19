package com.healthcare.bbc.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DesensitizeUtilTest {

    @Test
    void testDesensitizePhone() {
        assertEquals("138****8000", DesensitizeUtil.desensitizePhone("13800138000"));
        assertEquals("159****5678", DesensitizeUtil.desensitizePhone("15912345678"));
        assertEquals(null, DesensitizeUtil.desensitizePhone(null));
        assertEquals("123", DesensitizeUtil.desensitizePhone("123"));
    }

    @Test
    void testDesensitizeEmail() {
        assertEquals("te***@example.com", DesensitizeUtil.desensitizeEmail("test@example.com"));
        assertEquals("us***@domain.co.uk", DesensitizeUtil.desensitizeEmail("user.name@domain.co.uk"));
        assertEquals("a***@b.com", DesensitizeUtil.desensitizeEmail("ab@b.com"));
        assertEquals(null, DesensitizeUtil.desensitizeEmail(null));
        assertEquals("test", DesensitizeUtil.desensitizeEmail("test"));
    }

    @Test
    void testDesensitizeIdCard() {
        assertEquals("110101********1234", DesensitizeUtil.desensitizeIdCard("110101199001011234"));
        assertEquals("110101********123X", DesensitizeUtil.desensitizeIdCard("11010119900101123X"));
        assertEquals("110101********0123", DesensitizeUtil.desensitizeIdCard("110101900101123"));
        assertEquals(null, DesensitizeUtil.desensitizeIdCard(null));
        assertEquals("123", DesensitizeUtil.desensitizeIdCard("123"));
    }

    @Test
    void testDesensitizeName() {
        assertEquals("张*", DesensitizeUtil.desensitizeName("张三"));
        assertEquals("李*", DesensitizeUtil.desensitizeName("李四"));
        assertEquals("王**五", DesensitizeUtil.desensitizeName("王五五"));
        assertEquals("赵**六", DesensitizeUtil.desensitizeName("赵六六"));
        assertEquals(null, DesensitizeUtil.desensitizeName(null));
        assertEquals("A", DesensitizeUtil.desensitizeName("A"));
    }

    @Test
    void testDesensitizeBankCard() {
        assertEquals("6222********1234", DesensitizeUtil.desensitizeBankCard("62220200123456781234"));
        assertEquals("6222****1234", DesensitizeUtil.desensitizeBankCard("6222020012345678123"));
        assertEquals(null, DesensitizeUtil.desensitizeBankCard(null));
        assertEquals("1234567890", DesensitizeUtil.desensitizeBankCard("1234567890"));
    }

    @Test
    void testDesensitizeAddress() {
        assertEquals("北京市朝阳区******", DesensitizeUtil.desensitizeAddress("北京市朝阳区建国路88号"));
        assertEquals("上海市浦东新区******", DesensitizeUtil.desensitizeAddress("上海市浦东新区张江路100号"));
        assertEquals(null, DesensitizeUtil.desensitizeAddress(null));
        assertEquals("短地址", DesensitizeUtil.desensitizeAddress("短地址"));
    }
}
