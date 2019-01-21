package pl.szczep.blockchain.model;


import lombok.Builder;
import lombok.Getter;
import pl.szczep.blockchain.util.BlockchainValidator;
import pl.szczep.blockchain.util.DigitalSignature;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Block {

    public static String GENESIS_BLOCK_PREV_HASH = "0";

    @Getter
    private String hash;
    @Getter
    private String previousHash;

    public List<Transaction> transactions = new ArrayList<>();

    private long timeStamp;

    private int nonce;

    @Builder
    private Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = Instant.now().toEpochMilli();
        this.hash = calculateHash();
    }

    public String calculateHash() {

        return DigitalSignature.applySha256(
            previousHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce) +
                transactions.toString()
        );
    }

    public void mineBlock() {
        while (BlockchainValidator.isHashNotCompilantToDifficultyPolicy(this)) {
            nonce++;
            hash = calculateHash();
        }
    }

    public boolean addTransaction(Transaction transaction) {

        if (transaction == null)
            return false;

        if ((!isGenesisBlock())) {
            if ((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        hash = calculateHash();

        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    private boolean isGenesisBlock() {
        return previousHash.equals(GENESIS_BLOCK_PREV_HASH);
    }
}
