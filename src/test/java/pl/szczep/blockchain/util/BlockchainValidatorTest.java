package pl.szczep.blockchain.util;


import static org.assertj.core.api.Java6Assertions.assertThat;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;

public class BlockchainValidatorTest {

    private Blockchain blockchain;

    @Before
    public void setUpBlockchain(){
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
    public void shouldDetectBlockchainDataManipulation() throws IllegalAccessException {
        final Block block = blockchain.iterator().next();

        FieldUtils.writeField(block, "metaData",
                "Manipulate data", true);

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldDetectBlockchainDataManipulationWithHashTrick() throws IllegalAccessException {
        final Block block = blockchain.iterator().next();

        FieldUtils.writeField(block, "metaData", "Manipulate data", true);

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
}
