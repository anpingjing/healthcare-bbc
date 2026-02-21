package com.healthcare.bbc.util;

public class DesensitizeUtil {
    public static String desensitizePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    public static String desensitizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        if (prefix.isEmpty()) {
            return email;
        }
        int keep = prefix.length() <= 2 ? 1 : 2;
        return prefix.substring(0, keep) + "***" + email.substring(atIndex);
    }

    public static String desensitizeIdCard(String idCard) {
        if (idCard == null || (idCard.length() != 15 && idCard.length() != 18)) {
            return idCard;
        }
        if (idCard.length() == 15) {
            return idCard.substring(0, 6) + "********" + "0" + idCard.substring(12);
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    public static String desensitizeName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "**" + name.charAt(name.length() - 1);
    }

    public static String desensitizeBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() <= 10) {
            return bankCard;
        }
        if (bankCard.length() == 19) {
            return bankCard.substring(0, 4) + "****" + bankCard.substring(8, 12);
        }
        return bankCard.substring(0, 4) + "********" + bankCard.substring(bankCard.length() - 4);
    }

    public static String desensitizeAddress(String address) {
        if (address == null || address.length() < 10) {
            return address;
        }
        int idx = address.indexOf("区");
        int keep = idx >= 0 ? idx + 1 : 6;
        if (keep > address.length()) {
            keep = address.length();
        }
        return address.substring(0, keep) + "******";
    }
}
