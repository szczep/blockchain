package pl.szczep.blockchain.model;


import lombok.Builder;
import lombok.Getter;
import pl.szczep.blockchain.util.DigitalSignature;
import pl.szczep.blockchain.util.KeysHelper;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class Transaction {

    @Getter
    private PublicKey sender;
    @Getter
    private PublicKey recipient;
    @Getter
    private BigDecimal value;


    private byte[] signature;

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

    @Override
    public String toString() {
        return "Transaction{" +
                "sender=" + sender +
                ", recipient=" + recipient +
                ", value=" + value +
                '}';
    }
}