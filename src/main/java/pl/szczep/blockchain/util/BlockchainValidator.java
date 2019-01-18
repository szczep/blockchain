package pl.szczep.blockchain.util;


import lombok.experimental.UtilityClass;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;

@UtilityClass
public class BlockchainValidator {

    public static boolean validate(Blockchain blockchain) {

        String prevHash = "";
        for (Block block : blockchain) {

            if(isHashOfTheCurrentBlockInvalid(block) || isHashOfThePreviousBlockInvalid(block, prevHash)) {
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

}
