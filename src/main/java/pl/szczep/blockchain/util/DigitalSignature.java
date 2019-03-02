package pl.szczep.blockchain.util;


import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

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

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature signature;
        byte[] output = new byte[0];
        try {
            signature = getSignature();
            signature.initSign(privateKey);
            byte[] strByte = input.getBytes();
            signature.update(strByte);
            byte[] realSig = signature.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature signatureVerifier = getSignature();
            signatureVerifier.initVerify(publicKey);
            signatureVerifier.update(data.getBytes());
            return signatureVerifier.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Signature getSignature() throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance(KeysHelper.KEYS_ALGORITHM,
                        KeysHelper.KEYS_ALGORITHM_PROV);
    }
}
