package pl.szczep.blockchain.personal;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import pl.szczep.blockchain.model.Blockchain;
import pl.szczep.blockchain.model.Transaction;
import pl.szczep.blockchain.model.TransactionInput;
import pl.szczep.blockchain.model.TransactionOutput;
import pl.szczep.blockchain.util.KeysHelper;

@Getter
public class Wallet {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private Map<String, TransactionOutput> UTXOs =
            new HashMap<>();


    public Wallet() {
        KeyPair keyPair = KeysHelper.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public BigDecimal getBalance() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, TransactionOutput> item : Blockchain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.getId(), UTXO);
                total = total.add(UTXO.getValue());
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey _recipient, BigDecimal value) {
        if (getBalance().compareTo(value) < 0) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        List<TransactionInput> inputs = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;
        for (TransactionOutput UTXO: UTXOs.values()) {
            total = total.add(UTXO.getValue());
            inputs.add(TransactionInput.builder().transactionOutputId(UTXO.getId()).build());
            if (total.compareTo(value) > 0) break;
        }

        Transaction newTransaction = Transaction.builder()
                .from(publicKey)
                .to(_recipient)
                .value(value)
                .inputs(inputs)
                .build();

        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXOs.remove(input.getTransactionOutputId());
        }
        return newTransaction;
    }
}
