package com.healthcare.bbc.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptUtil {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "healthcare-bbc-encryption-key-2026";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public String encrypt(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }

    public String encryptPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return encrypt(phone);
    }

    public String decryptPhone(String encryptedPhone) {
        if (encryptedPhone == null) {
            return null;
        }
        try {
            return decrypt(encryptedPhone);
        } catch (Exception e) {
            return encryptedPhone;
        }
    }

    public String encryptIdCard(String idCard) {
        if (idCard == null || (idCard.length() != 15 && idCard.length() != 18)) {
            return idCard;
        }
        return encrypt(idCard);
    }

    public String decryptIdCard(String encryptedIdCard) {
        if (encryptedIdCard == null) {
            return null;
        }
        try {
            return decrypt(encryptedIdCard);
        } catch (Exception e) {
            return encryptedIdCard;
        }
    }
}
