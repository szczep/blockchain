package pl.szczep.blockchain.model;


import lombok.Builder;
import lombok.Getter;
import pl.szczep.blockchain.util.BlockchainValidator;
import pl.szczep.blockchain.util.DigitalSignature;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Block {

    @Getter
    private String hash;
    @Getter
    private String previousHash;

    public String merkleRoot;
    public List<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.

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
                    merkleRoot
        );
    }

    public void mineBlock() {
        while (BlockchainValidator.isHashNotCompilantToDifficultyPolicy(this)) {
            nonce++;
            hash = calculateHash();
        }
    }


    public boolean addTransaction(Transaction transaction) {
        if(transaction == null) return false;
        if((!previousHash.equals("0"))) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        merkleRoot = DigitalSignature.getMerkleRoot(transactions);
        hash = calculateHash();

        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
