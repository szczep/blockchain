package pl.szczep.blockchain.util;

import lombok.experimental.UtilityClass;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

@UtilityClass
public class KeysHelper {

    public static final String KEYS_ALGORITHM = "ECDSA";
    public static final String KEYS_ALGORITHM_PROV = "BC";


    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEYS_ALGORITHM, KEYS_ALGORITHM_PROV);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);

            return keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}