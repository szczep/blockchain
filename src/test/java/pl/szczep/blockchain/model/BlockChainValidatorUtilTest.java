package pl.szczep.blockchain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BlockChainValidatorUtilTest {


    @Test
    public void shouldBlockChainBeValid() {

        List<Block> validBlockChain = new ArrayList<Block>();
        validBlockChain.add(new Block("First block", "0"));
        validBlockChain.add(new Block("Second block", validBlockChain.get(validBlockChain.size() - 1).getHash()));
        validBlockChain.add(new Block("Third block", validBlockChain.get(validBlockChain.size() - 1).getHash()));

        assertThat(BlockChainValidatorUtil.isChainValid(validBlockChain)).isTrue();
    }

    @Test
    public void shouldBlockChainBeInvalid() {

        List<Block> validBlockChain = new ArrayList<Block>();
        validBlockChain.add(new Block("First block", "0"));
        validBlockChain.add(new Block("Second block", "@#%@$%J#$OGERNER"));
        validBlockChain.add(new Block("Third block", validBlockChain.get(validBlockChain.size() - 1).getHash()));

        assertThat(BlockChainValidatorUtil.isChainValid(validBlockChain)).isFalse();
    }


}