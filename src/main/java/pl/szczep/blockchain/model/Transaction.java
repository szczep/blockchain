package pl.szczep.blockchain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.szczep.blockchain.util.DigitalSignature;
import pl.szczep.blockchain.util.KeysHelper;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    public static String GENESIS_TRANSACTION_HASH = "0";


    @Getter
    @Setter
    private String transactionId;
    @Getter
    private PublicKey sender;
    @Getter
    private PublicKey recipient;
    @Getter
    private BigDecimal value;

    @Getter
    private List<TransactionInput> inputs = new ArrayList<>();
    @Getter
    private List<TransactionOutput> outputs = new ArrayList<>();

    private byte[] signature;

    private static long sequence = 0;

    @Builder
    private Transaction(PublicKey from,
                        PublicKey to,
                        BigDecimal value,
                        List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
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

    public boolean processTransaction() {

        if (verifyIfTransactionHasValidSignature()) { return false; }

        for (TransactionInput i : inputs) {
            i.setUTXO(Blockchain.getUTXO(i.getTransactionOutputId()));
        }

        BigDecimal leftOver = getInputsValue().subtract(value);
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        outputs.forEach(Blockchain::addUTXO);
        inputs.forEach(inT -> Blockchain.removeUTXO(inT.getUTXO().getId()));

        return true;
    }

    public BigDecimal getInputsValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null) { continue; }
            total = total.add(i.getUTXO().getValue());
        }
        return total;
    }

    public BigDecimal getOutputsValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionOutput o : outputs) {
            total = total.add(o.getValue());
        }
        return total;
    }

    private boolean verifyIfTransactionHasValidSignature() {
        if (!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return true;
        }
        return false;
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