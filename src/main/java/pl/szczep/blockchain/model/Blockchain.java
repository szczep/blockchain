package pl.szczep.blockchain.model;


import java.util.Iterator;
import java.util.List;

import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Singular;

@Builder
public class Blockchain implements Iterable<Block> {

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
