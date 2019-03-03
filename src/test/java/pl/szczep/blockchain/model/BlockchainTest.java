package pl.szczep.blockchain.model;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BlockchainTest {


    @Test
    public void shouldContainThreeBlocks() {

        final Block block1 = Block.builder().metaData("Block #1").previousHash(Block.GENESIS_HASH).build();
        final Block block2 = Block.builder().metaData("Block #2").previousHash(block1.getHash()).build();
        final Block block3 = Block.builder().metaData("Block #3").previousHash(block2.getHash()).build();

        final Blockchain blockchain = Blockchain.builder()
            .block(block1)
            .block(block2)
            .block(block3)
            .build();

        assertThat(blockchain.size()).isEqualTo(3);
        assertThat(blockchain.getLastHash()).isEqualTo(block3.getHash());
        System.out.println(blockchain);
    }
}