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

    @Getter
    @Setter
    private String transactionId;
    @Getter
    private PublicKey sender;
    @Getter
    private PublicKey recipient;
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
        String data = KeysHelper.getStringFromKey(sender) +
                KeysHelper.getStringFromKey(recipient) + value.toString();
        signature = DigitalSignature.applyECDSASig(privateKey, data);
    }

    public boolean verifiySignature() {
        String data = KeysHelper.getStringFromKey(sender) +
                KeysHelper.getStringFromKey(recipient) + value.toString();
        return DigitalSignature.verifyECDSASig(sender, data, signature);
    }


    public boolean processTransaction() {

        if (verifyIfTransactionHasValidSignature()) return false;

        for (TransactionInput i : inputs) {
            i.setUTXO(Blockchain.UTXOs.get(i.getTransactionOutputId()));
        }

        //check if transaction is valid:
        if (getInputsValue().compareTo(Blockchain.minimumTransaction) < 0) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        BigDecimal leftOver = getInputsValue().subtract(value);
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));


        for (TransactionOutput o : outputs) {
            Blockchain.UTXOs.put(o.getId(), o);
        }

        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null) continue;
            Blockchain.UTXOs.remove(i.getUTXO().getId());
        }

        return true;
    }

    public BigDecimal getInputsValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null) continue;
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
        if (!verifiySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return true;
        }
        return false;
    }

    private String calulateHash() {
        sequence++;
        return DigitalSignature.applySha256(
                KeysHelper.getStringFromKey(sender) +
                        KeysHelper.getStringFromKey(recipient) +
                        value.toString() + sequence
        );
    }
}