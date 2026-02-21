package com.healthcare.bbc.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptUtil {
    private static final String V1_PREFIX = "v1:";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    private final SecretKeySpec currentKey;
    private final SecretKeySpec previousKey;
    private final SecretKeySpec legacyKey;

    public EncryptUtil(
            @Value("${encryption.key}") String encryptionKey,
            @Value("${encryption.previous-key:}") String previousEncryptionKey,
            @Value("${encryption.legacy-key:}") String legacyEncryptionKey
    ) {
        this.currentKey = toAesKey(encryptionKey);
        this.previousKey = previousEncryptionKey == null || previousEncryptionKey.isBlank() ? null : toAesKey(previousEncryptionKey);
        this.legacyKey = legacyEncryptionKey == null || legacyEncryptionKey.isBlank() ? null : toAesKey(legacyEncryptionKey);
    }

    private SecretKeySpec toAesKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("encryption.key is required");
        }
        byte[] raw;
        if (key.startsWith("base64:")) {
            raw = Base64.getDecoder().decode(key.substring("base64:".length()));
        } else {
            raw = key.getBytes(StandardCharsets.UTF_8);
        }
        if (raw.length != 16 && raw.length != 24 && raw.length != 32) {
            throw new IllegalStateException("encryption key length must be 16/24/32 bytes");
        }
        return new SecretKeySpec(raw, "AES");
    }

    private String encryptV1(String data, SecretKeySpec key) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return V1_PREFIX + Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    private String decryptV1(String encryptedData, SecretKeySpec key) {
        try {
            String payload = encryptedData.substring(V1_PREFIX.length());
            String[] parts = payload.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("invalid payload");
            }
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] ciphertext = Base64.getDecoder().decode(parts[1]);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] decrypted = cipher.doFinal(ciphertext);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }

    private String decryptLegacyEcb(String encryptedData, SecretKeySpec key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }

    public String encrypt(String data) {
        if (data == null) {
            return null;
        }
        return encryptV1(data, currentKey);
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }
        if (encryptedData.startsWith(V1_PREFIX)) {
            try {
                return decryptV1(encryptedData, currentKey);
            } catch (Exception e) {
                if (previousKey != null) {
                    return decryptV1(encryptedData, previousKey);
                }
                throw e;
            }
        }
        if (legacyKey != null) {
            return decryptLegacyEcb(encryptedData, legacyKey);
        }
        throw new RuntimeException("解密失败");
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
