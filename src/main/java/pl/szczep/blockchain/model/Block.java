package pl.szczep.blockchain.model;


import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.szczep.blockchain.util.DigitalSignature;


public class Block {

    @Getter
    private String hash;
    @Getter
    private String previousHash;
    @Getter @Setter
    private String data;
    private long timeStamp;

    private int nonce;

    @Builder
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = Instant.now().toEpochMilli();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return DigitalSignature.applySha256(
            previousHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce) +
                data
        );
    }

    public void mineBlock(int difficulty) {
        while (!BlockChainValidatorUtil.isHashCompilantToDifficultyPolicy(hash)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }
}
