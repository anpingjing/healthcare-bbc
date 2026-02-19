package com.healthcare.admin.common.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-for-jwt-util-test-2026");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 7200000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 604800000L);
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";

        // When
        String token = jwtUtil.generateAccessToken(userId, username);

        // Then
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.validateAccessToken(token));
        assertFalse(jwtUtil.validateRefreshToken(token));

        // Verify claims
        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
        assertEquals("access", jwtUtil.getTokenType(token));
    }

    @Test
    void testGenerateAndValidateRefreshToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";

        // When
        String token = jwtUtil.generateRefreshToken(userId, username);

        // Then
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.validateAccessToken(token));
        assertTrue(jwtUtil.validateRefreshToken(token));

        // Verify claims
        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
        assertEquals("refresh", jwtUtil.getTokenType(token));
    }

    @Test
    void testParseInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        Claims claims = jwtUtil.parseToken(invalidToken);

        // Then
        assertNull(claims);
    }

    @Test
    void testValidateInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertFalse(jwtUtil.validateToken(invalidToken));
        assertFalse(jwtUtil.validateAccessToken(invalidToken));
        assertFalse(jwtUtil.validateRefreshToken(invalidToken));
    }

    @Test
    void testGetExpiration() {
        // When & Then
        assertEquals(7200000L, jwtUtil.getAccessTokenExpiration());
        assertEquals(604800000L, jwtUtil.getRefreshTokenExpiration());
    }
}
