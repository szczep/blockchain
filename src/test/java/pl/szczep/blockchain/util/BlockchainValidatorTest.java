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
        final Block block1 = Block.builder().data("Block #1").previousHash("").build();
        final Block block2 = Block.builder().data("Block #2").previousHash(block1.getHash()).build();
        final Block block3 = Block.builder().data("Block #3").previousHash(block2.getHash()).build();

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
        block.setData("Manipulate data");

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }

    @Test
    public void shouldDetectBlockchainDataManipulationWithHashTrick() throws IllegalAccessException {
        final Block block = blockchain.iterator().next();
        block.setData("Manipulate data");
        FieldUtils.writeField(block, "hash", block.calculateHash(), true);

        assertThat(BlockchainValidator.validate(blockchain)).isFalse();
    }
}