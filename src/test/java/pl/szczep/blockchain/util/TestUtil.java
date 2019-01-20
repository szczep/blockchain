package pl.szczep.blockchain.util;

import pl.szczep.blockchain.model.Blockchain;
import pl.szczep.blockchain.model.Transaction;
import pl.szczep.blockchain.model.TransactionInput;
import pl.szczep.blockchain.model.TransactionOutput;
import pl.szczep.blockchain.personal.Wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

public class TestUtil {

    private static Wallet sender;
    private static Wallet recipient;
    private static Transaction transaction;


    public static Transaction generateMockedTransaction() {

        sender = new Wallet();
        recipient = new Wallet();

        TransactionInput transactionInput = TransactionInput.builder()
                .transactionOutputId("id1")
                .UTXO(TransactionOutput.builder()
                        .id("ido1")
                        .recipient(sender.getPublicKey())
                        .parentTransactionId("idp1")
                        .value(BigDecimal.TEN)
                        .build())
                .build();

        transaction = Transaction.builder()
                .from(sender.getPublicKey())
                .to(recipient.getPublicKey())
                .value(BigDecimal.TEN)
                .inputs(new ArrayList<>(
                        Collections.singletonList(transactionInput)))
                .build();

        transaction.generateSignature(sender.getPrivateKey());

        Blockchain.UTXOs.put(transactionInput.getTransactionOutputId(), transactionInput.getUTXO());

        return transaction;
    }


    public static Transaction createGenesisTransaction() {

        Wallet coinbase = new Wallet();
        Wallet walletA = new Wallet();


        Transaction genesisTransaction = Transaction.builder()
                .from(coinbase.getPublicKey())
                .to(walletA.getPublicKey())
                .value(BigDecimal.TEN)
                .inputs(new ArrayList<>())
                .build();

        TransactionOutput transactionOutput = TransactionOutput.builder()
                .id("ido1")
                .recipient(coinbase.getPublicKey())
                .parentTransactionId(genesisTransaction.getTransactionId())
                .value(BigDecimal.TEN)
                .build();


        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        genesisTransaction.getOutputs().add(transactionOutput);

        if (Blockchain.UTXOs.isEmpty())
            Blockchain.UTXOs.put(transactionOutput.getId(), transactionOutput);

        return genesisTransaction;
    }

}
