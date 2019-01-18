package pl.szczep.blockchain.util;


import java.security.MessageDigest;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DigitalSignature {

    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            return convertBytesToHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertBytesToHexString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte aHash : hash) {
            String hex = Integer.toHexString(0xff & aHash);
            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }


}
