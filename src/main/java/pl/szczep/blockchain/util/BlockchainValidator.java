package pl.szczep.blockchain.util;


import lombok.Getter;
import lombok.experimental.UtilityClass;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;

@UtilityClass
public class BlockchainValidator {

    @Getter
    private static int DIFFICULTY = 0;
    @Getter
    private static String DIFFICULTY_PREFIX = new String(new char[DIFFICULTY]).replace('\0', '0');


    public static void setDifficulty(int difficulty) {
        DIFFICULTY = difficulty;
        DIFFICULTY_PREFIX = new String(new char[DIFFICULTY]).replace('\0', '0');
    }

    public static boolean validate(Blockchain blockchain) {

        String prevHash = "";
        for (Block block : blockchain) {

            if (isHashOfTheCurrentBlockInvalid(block) ||
                    isHashOfThePreviousBlockInvalid(block, prevHash) ||
                    isHashNotCompilantToDifficultyPolicy(block)) {
                return false;
            }
            prevHash = block.getHash();
        }

        return true;
    }

    private static boolean isHashOfThePreviousBlockInvalid(Block block, String previousHash) {
        return !block.getPreviousHash().equals(previousHash);

    }

    private static boolean isHashOfTheCurrentBlockInvalid(Block block) {
        return !block.getHash().equals(block.calculateHash());
    }

    public static Boolean isHashNotCompilantToDifficultyPolicy(Block block) {
        return !block.getHash().substring(0, DIFFICULTY).equals(DIFFICULTY_PREFIX);
    }

}
