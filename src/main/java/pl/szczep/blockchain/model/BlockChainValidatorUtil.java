package pl.szczep.blockchain.model;


import java.util.List;

public class BlockChainValidatorUtil {

    private static int difficulty = 4;
    private static String target = new String(new char[difficulty]).replace('\0', '0');


    public static Boolean isChainValid(List<Block> blockchain) {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }

    public static Boolean isHashCompilantToDifficultyPolicy(String hash) {
        return hash.substring(0, difficulty).equals(target);
    }

}
