package pl.szczep.blockchain;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import pl.szczep.blockchain.model.Block;

public class TestBlockChain {

    public static List<Block> blockchain = new ArrayList<Block>();

    public static void main(String[] args) {

        //add our blocks to the blockchain ArrayList:
        blockchain.add(new Block("Hi im the first block", "0"));
        blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size() - 1).getHash()));
        blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size() - 1).getHash()));

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

    }
}
