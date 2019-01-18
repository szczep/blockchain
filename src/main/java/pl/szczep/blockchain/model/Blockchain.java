package pl.szczep.blockchain.model;


import java.util.List;

import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Singular;

@Builder
public class Blockchain {

    @Singular
    List<Block> blocks;

    public String getLastHash() {
        return blocks.size() > 0 ? blocks.get(blocks.size() - 1).getHash() : "";
    }

    public int size() {
        return blocks.size();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(blocks);
    }


}
