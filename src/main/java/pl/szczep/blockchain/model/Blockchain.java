package pl.szczep.blockchain.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Builder
public class Blockchain implements Iterable<Block> {

    public static Map<String, TransactionOutput> UTXOs =
        new HashMap<String, TransactionOutput>();

    public static void addUTXO(TransactionOutput transactionOutput) {
        UTXOs.put(transactionOutput.getId(), transactionOutput);
    }

    public static TransactionOutput getUTXO(String transactionOutputId) {
        return UTXOs.get(transactionOutputId);
    }

    public static TransactionOutput removeUTXO(String transactionOutputId) {
        return UTXOs.remove(transactionOutputId);
    }


    @Getter
    @Setter
    private Transaction genesisTransaction;

    @Singular
    private List<Block> blocks;

    public int size() {
        return blocks.size();
    }

    public boolean addBlock(Block block) {
        block.mineBlock();
        blocks = new ArrayList<>(blocks);
        return blocks.add(block);
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(blocks);
    }

    @Override
    public Iterator<Block> iterator() {
        return new Iterator<Block>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                return position < blocks.size();
            }

            @Override
            public Block next() {
                return this.hasNext() ? blocks.get(position++) : null;
            }
        };
    }
}
