package pl.szczep.model;


import java.time.Instant;

import pl.szczep.util.DigitalSignature;

public class Block {

    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;

    private int nonce;

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
