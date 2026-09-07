package com.example.apptareas.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import android.util.Base64;

public class SecurityUtils {

    // Hashea contraseña con SHA-256 + salt
    public static String[] hashPassword(String password) {
        String salt = generateSalt();
        String combined = password + salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes());
            String hash = Base64.encodeToString(hashBytes, Base64.NO_WRAP);
            return new String[]{hash, salt};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"", ""};
        }
    }

    // Verifica contraseña
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String[] result = hashPasswordWithSalt(password, salt);
        return result[0].equals(storedHash);
    }

    private static String[] hashPasswordWithSalt(String password, String salt) {
        String combined = password + salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes());
            String hash = Base64.encodeToString(hashBytes, Base64.NO_WRAP);
            return new String[]{hash, salt};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"", ""};
        }
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP);
    }
}