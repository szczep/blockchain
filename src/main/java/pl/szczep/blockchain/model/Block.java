package pl.szczep.blockchain.model;


import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import pl.szczep.blockchain.util.BlockchainValidator;
import pl.szczep.blockchain.util.DigitalSignature;

import java.util.ArrayList;
import java.util.List;


public class Block {

    public static String GENESIS_HASH = "0";

    @Getter
    private String previousHash;

    @Getter
    private String metaData;

    @Getter
    private List<Transaction> transactions;

    @Getter
    private String hash;

    private int nonce;

    @Builder
    public Block(String previousHash, String metaData, List<Transaction> transactions) {

        this.transactions = transactions;
        if (this.transactions == null)
            this.transactions = new ArrayList<>();

        this.previousHash = previousHash;
        this.metaData = metaData;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return DigitalSignature.applySha256(
                previousHash +
                        Integer.toString(nonce) +
                        metaData +
                        transactions);
    }

    public void mineBlock() {
        while (BlockchainValidator.isHashNotCompilantToDifficultyPolicy(this)) {
            nonce++;
            hash = calculateHash();
        }
    }

    public void mineFromStart() {
        nonce = 0;
        hash = calculateHash();

        while (BlockchainValidator.isHashNotCompilantToDifficultyPolicy(this)) {
            nonce++;
            hash = calculateHash();
        }
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
