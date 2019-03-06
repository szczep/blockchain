package pl.szczep.blockchain.util;


import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.log4j.Logger;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;
import pl.szczep.blockchain.model.Transaction;

import java.util.List;

@UtilityClass
public class BlockchainValidator {

    private static final Logger LOG = Logger.getLogger(BlockchainValidator.class);

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

            if (
                isHashOfTheCurrentBlockInvalid(block) ||
                    isHashOfThePreviousBlockInvalid(block, prevHash) ||
                    isAnyTransactionInvalid(block.getTransactions())
//                    isHashOfTheCurrentBlockNotCompilantToDifficultyPolicy(block)

                ) {
                return false;
            }
            prevHash = block.getHash();
        }

        return true;
    }

    private static boolean isAnyTransactionInvalid(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (!transaction.verifySignature()) {
                LOG.warn("The transaction is invalid");
                return true;
            }
        }

        return false;
    }

    public static Boolean isHashNotCompilantToDifficultyPolicy(Block block) {
        return !block.getHash().substring(0, DIFFICULTY).equals(DIFFICULTY_PREFIX);
    }

    private static boolean isHashOfThePreviousBlockInvalid(Block block, String previousHash) {
        final boolean isInValid = !block.getPreviousHash().equals(previousHash);
        if (isInValid) LOG.warn("The hash of the previous block is invalid.");
        return isInValid;

    }

    private static boolean isHashOfTheCurrentBlockInvalid(Block block) {
        final boolean isInValid = !block.getHash().equals(block.calculateHash());
        if (isInValid) LOG.warn("The hash of the block is invalid.");
        return isInValid;
    }


    private static Boolean isHashOfTheCurrentBlockNotCompilantToDifficultyPolicy(Block block) {
        final boolean isInValid = isHashNotCompilantToDifficultyPolicy(block);
        if (isInValid) LOG.warn("The block was not mined");
        return isInValid;
    }
}
