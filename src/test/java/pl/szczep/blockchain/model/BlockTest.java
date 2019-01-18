package pl.szczep.blockchain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import pl.szczep.blockchain.util.BlockchainValidator;


public class BlockTest {

    @Test
    public void shouldChangeHashForDifferentTimeCreations() throws InterruptedException {
        final Block block1 = Block.builder().data("Hello Blockchain").previousHash("").build();
        TimeUnit.SECONDS.sleep(1);
        final Block block2 = Block.builder().data("Hello Blockchain").previousHash("").build();

        assertThat(block1.getHash()).isNotEqualTo(block2.getHash());
    }

    @Test
    public void shouldChangeHashWhenDataChanged() {
        final Block block = Block.builder().data("Hello Blockchain").previousHash("").build();

        final String hash1 = block.calculateHash();
        block.setData("Hello Blockchain!");
        final String hash2 = block.calculateHash();

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    public void shouldMineBlock(){
        BlockchainValidator.setDifficulty(4);

        final Block block = Block.builder().data("Hello Blockchain").previousHash("").build();
        block.mineBlock();

        assertThat(block.getHash()).startsWith(BlockchainValidator.getDIFFICULTY_PREFIX());
        System.out.println(block.getHash());
    }
}
