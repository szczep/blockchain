package pl.szczep.blockchain.util;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;
import pl.szczep.blockchain.model.Transaction;

import java.security.Security;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class BlockchainValidatorTest {

    private Blockchain blockchain;
    private static Transaction transaction;


    @BeforeClass
    public static void setUp() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        transaction = TestUtil.generateMockedTransaction();
    }

    @Before
    public void setUpBlockchain() {
        BlockchainValidator.setDifficulty(0);

        final Block block1 = Block.builder().previousHash("").build();
        final Block block2 = Block.builder().previousHash(block1.getHash()).build();
        final Block block3 = Block.builder().previousHash(block2.getHash()).build();

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
        assertThat(block.addTransaction(transaction)).isTrue();

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldDetectBlockchainDataManipulationWithHashTrick() throws IllegalAccessException {
        final Block block = blockchain.iterator().next();
        assertThat(block.addTransaction(transaction)).isTrue();
        FieldUtils.writeField(block, "hash", block.calculateHash(), true);

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldValidateCorrectBlockchainWithDifficulty() {
        BlockchainValidator.setDifficulty(3);
        setUpBlockchainWithMining();

        assertThat(BlockchainValidator.validate(blockchain)).isTrue();
    }

    @Test
    public void shouldValidateInCorrectBlockchainWithoutMining() {
        BlockchainValidator.setDifficulty(3);

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    private void setUpBlockchainWithMining() {
        final Block block1 = Block.builder().previousHash("").build();
        block1.mineBlock();
        final Block block2 = Block.builder().previousHash(block1.getHash()).build();
        block2.mineBlock();
        final Block block3 = Block.builder().previousHash(block2.getHash()).build();
        block3.mineBlock();

        blockchain = Blockchain.builder()
                .block(block1)
                .block(block2)
                .block(block3)
                .build();
    }
}
