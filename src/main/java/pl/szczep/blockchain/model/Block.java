package pl.szczep.blockchain.model;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.szczep.blockchain.util.BlockchainValidator;
import pl.szczep.blockchain.util.DigitalSignature;


public class Block {

    @Getter
    private String hash;
    @Getter
    private String previousHash;
    @Getter
    @Setter
    public String metaData;
    @Getter
    @Setter
    public List<Transaction> transactions = new ArrayList<>();
    private long timeStamp;

    private int nonce;

    @Builder
    public Block(String metaData, String previousHash, List<Transaction> transactions) {
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.timeStamp = Instant.now().toEpochMilli();
        this.metaData = metaData;
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
