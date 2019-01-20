package pl.szczep.blockchain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import pl.szczep.blockchain.util.BlockchainValidator;
import pl.szczep.blockchain.util.BlockchainValidatorTest;
import pl.szczep.blockchain.util.TestUtil;


public class BlockTest {

    @BeforeClass
    public static void setUp(){
        BlockchainValidatorTest.setUp();
    }

    @Test
    public void shouldChangeHashForDifferentTimeCreations() throws InterruptedException {
        final Block block1 = Block.builder().previousHash("").build();
        TimeUnit.SECONDS.sleep(1);
        final Block block2 = Block.builder().previousHash("").build();

        assertThat(block1.getHash()).isNotEqualTo(block2.getHash());
    }

    @Test
    public void shouldChangeHashWhenDataChanged() {
        final Block block = Block.builder().previousHash("").build();
        block.addTransaction(TestUtil.generateMockedTransaction());

        final String hash1 = block.calculateHash();
        block.addTransaction(TestUtil.generateMockedTransaction());
        final String hash2 = block.calculateHash();

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    public void shouldMineBlock(){
        BlockchainValidator.setDifficulty(4);

        final Block block = Block.builder().previousHash("").build();
        block.mineBlock();

        assertThat(block.getHash()).startsWith(BlockchainValidator.getDIFFICULTY_PREFIX());
        System.out.println(block.getHash());
    }
}
