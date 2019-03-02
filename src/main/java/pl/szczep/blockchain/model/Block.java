package pl.szczep.blockchain.model;


import com.google.gson.GsonBuilder;
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
    private String previousHash;

    @Getter
    private String metaData;

    @Getter
    private List<Transaction> transactions = new ArrayList<>();

    @Getter
    private String hash;
    private long timeStamp;

    private int nonce;

    @Builder
    public Block(String previousHash, String metaData, List<Transaction> transactions) {
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.metaData = metaData;
        this.timeStamp = Instant.now().toEpochMilli();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return DigitalSignature.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        metaData +
                        transactions
        );
    }

    public void mineBlock() {
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
