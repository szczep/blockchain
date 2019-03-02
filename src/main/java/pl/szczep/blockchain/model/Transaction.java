package pl.szczep.blockchain.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.szczep.blockchain.util.DigitalSignature;
import pl.szczep.blockchain.util.KeysHelper;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Transaction {

    public static String GENESIS_TRANSACTION_HASH = "0";


    @Getter
    private PublicKey sender;
    @Getter
    private PublicKey recipient;
    @Getter
    private BigDecimal value;


    private byte[] signature;

    private static long sequence = 0;

    @Builder
    private Transaction(PublicKey from,
                        PublicKey to,
                        BigDecimal value) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
    }

    public void generateSignature(PrivateKey privateKey) {
        String transactionData = KeysHelper.getStringFromKey(sender) +
                KeysHelper.getStringFromKey(recipient) + value.toString();
        signature = DigitalSignature.applyECDSASig(privateKey, transactionData);
    }

    public boolean verifySignature() {
        String transactionData = KeysHelper.getStringFromKey(sender) +
                KeysHelper.getStringFromKey(recipient) + value.toString();
        return DigitalSignature.verifyECDSASig(sender, transactionData, signature);
    }


    private String calculateHash() {
        sequence++;
        return DigitalSignature.applySha256(
                KeysHelper.getStringFromKey(sender) +
                        KeysHelper.getStringFromKey(recipient) +
                        value.toString() + sequence
        );
    }
}