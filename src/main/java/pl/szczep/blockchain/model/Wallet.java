package pl.szczep.blockchain.model;


import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import lombok.Getter;
import pl.szczep.blockchain.util.KeysHelper;

@Getter
public class Wallet {

    private PrivateKey privateKey;
    private PublicKey publicKey;


    public Wallet() {
        KeyPair keyPair = KeysHelper.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
}
