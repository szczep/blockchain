package pl.szczep.blockchain.model;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import pl.szczep.blockchain.util.BlockchainValidator;

import static org.assertj.core.api.Assertions.assertThat;


public class BlockTest {


    @Test
    public void shouldChangeHashWhenDataChanged() throws IllegalAccessException {
        final Block block = Block.builder()
                .metaData("Block1")
                .previousHash("").build();

        final String hash1 = block.calculateHash();
        FieldUtils.writeField(block, "metaData",
                "Blocked hacked", true);

        final String hash2 = block.calculateHash();

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    public void shouldMineBlock() {
        BlockchainValidator.setDifficulty(4);

        final Block block = Block.builder().metaData("Hello Blockchain").previousHash("").build();
        block.mineBlock();

        assertThat(block.getHash()).startsWith(BlockchainValidator.getDIFFICULTY_PREFIX());
        System.out.println(block.getHash());
    }
}
