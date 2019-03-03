package pl.szczep.blockchain.util;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;
import pl.szczep.blockchain.model.Transaction;
import pl.szczep.blockchain.model.Wallet;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class BlockchainValidatorTest {

    private Blockchain blockchain;

    @Before
    public void setUpBlockchain(){
        BasicConfigurator.configure();

        BlockchainValidator.setDifficulty(0);

        final Block block1 = Block.builder().metaData("Block #1").previousHash(Block.GENESIS_HASH).build();
        final Block block2 = Block.builder().metaData("Block #2").previousHash(block1.getHash()).build();
        final Block block3 = Block.builder().metaData("Block #3").previousHash(block2.getHash()).build();

        blockchain = Blockchain.builder()
            .block(block1)
            .block(block2)
            .block(block3)
            .build();
    }

    @Test
    public void shouldValidateCorrectBlockchain() {
        assertThat(BlockchainValidator.validate(blockchain)).isTrue();
    }

    @Test
    public void shouldDetectBlockchainDataManipulation() {
        final Block block = blockchain.iterator().next();

        changeField(block, "metaData", "Manipulate data");

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldDetectBlockchainDataManipulationWithHashTrick() {
        final Block block = blockchain.iterator().next();

        changeField(block, "metaData", "Manipulate data");

        changeField(block, "hash", block.calculateHash());

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldInvalidBlockchainWithInvalidTransaction() {
        assertThat(BlockchainValidator.validate(blockchain)).isTrue();

        Block block = blockchain.getBlock(2);
        Wallet personA = new Wallet();
        Wallet personB = new Wallet();
        Transaction tr = Transaction.builder()
                .from(personA.getPublicKey())
                .to(personB.getPublicKey())
                .value(new BigDecimal("10"))
                .build();
        tr.generateSignature(personB.getPrivateKey());

        changeField(block, "transactions", Collections.singletonList(tr));

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
        changeField(block, "hash", block.calculateHash());

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldNotDetectBlockManipulationWhenAllHashesFixed() {
        final Block block = blockchain.getBlock(1);
        final Block block2 = blockchain.getBlock(2);

        changeField(block, "metaData", "Manipulate data");
        changeField(block, "hash", block.calculateHash());

        changeField(block2, "previousHash", block.getHash());
        changeField(block2, "hash", block2.calculateHash());

        assertThat(BlockchainValidator.validate(blockchain)).isTrue();
    }

    @Test
    public void shouldValidateInCorrectBlockchainWithoutMining() {
        BlockchainValidator.setDifficulty(3);

        final Block block = blockchain.getBlock(1);
        final Block block2 = blockchain.getBlock(2);

        changeField(block, "metaData", "Manipulate data");
        changeField(block, "hash", block.calculateHash());

        changeField(block2, "previousHash", block.getHash());
        changeField(block2, "hash", block2.calculateHash());

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldValidateCorrectBlockchainWithDifficulty() {
        BlockchainValidator.setDifficulty(3);
        setUpBlockchainWithMining();

        final Block block = blockchain.getBlock(1);
        final Block block2 = blockchain.getBlock(2);

        changeField(block, "metaData", "Manipulate data");
        changeField(block, "hash", block.calculateHash());
        block.mineBlock();

        changeField(block2, "previousHash", block.getHash());
        changeField(block2, "hash", block2.calculateHash());
        block2.mineBlock();


        assertThat(BlockchainValidator.validate(blockchain)).isTrue();
    }

    private void setUpBlockchainWithMining() {
        final Block block1 = Block.builder().metaData("Block #1").previousHash(Block.GENESIS_HASH).build();
        block1.mineBlock();
        final Block block2 = Block.builder().metaData("Block #2").previousHash(block1.getHash()).build();
        block2.mineBlock();
        final Block block3 = Block.builder().metaData("Block #3").previousHash(block2.getHash()).build();
        block3.mineBlock();

        blockchain = Blockchain.builder()
            .block(block1)
            .block(block2)
            .block(block3)
            .build();
    }

    private void changeField(Object o, String field, Object newValue) {
        try {
            FieldUtils.writeField(o, field, newValue, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
