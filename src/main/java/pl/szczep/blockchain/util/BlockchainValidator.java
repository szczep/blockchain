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

        String prevHash = Block.GENESIS_HASH;
        for (Block block : blockchain) {

            if (isHashOfTheCurrentBlockInvalid(block) || isHashOfThePreviousBlockInvalid(block, prevHash) ||
                    isHashNotCompilantToDifficultyPolicy(block)) {
                return false;
            }
            prevHash = block.getHash();
        }

        return true;
    }

    private static boolean isHashOfThePreviousBlockInvalid(Block block, String previousHash) {
        final boolean isInValid = !block.getPreviousHash().equals(previousHash);
        if (isInValid) System.out.println("The hash of the previous block is invalid.");
        return isInValid;

    }

    private static boolean isHashOfTheCurrentBlockInvalid(Block block) {
        final boolean isInValid = !block.getHash().equals(block.calculateHash());
        if (isInValid) System.out.println("The hash of the block is invalid.");
        return isInValid;
    }

    public static Boolean isHashNotCompilantToDifficultyPolicy(Block block) {
        final boolean isInValid = !block.getHash().substring(0, DIFFICULTY).equals(DIFFICULTY_PREFIX);
        if (isInValid) System.out.println("The block was not mined");
        return isInValid;
    }
}
