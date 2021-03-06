package pl.szczep.blockchain.util;


import lombok.Getter;
import lombok.experimental.UtilityClass;
import pl.szczep.blockchain.model.*;

import java.util.HashMap;
import java.util.Map;

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

        String prevHash = Block.GENESIS_BLOCK_PREV_HASH;
        for (Block block : blockchain) {

            if (isHashOfTheCurrentBlockInvalid(block) ||
                    isHashOfThePreviousBlockInvalid(block, prevHash) ||
                    isHashNotCompilantToDifficultyPolicy(block)) {
                return false;
            }

            TransactionOutput tempOutput;
            Map<String,TransactionOutput> tempUTXOs = new HashMap<>();
            tempUTXOs.put(blockchain.getGenesisTransaction().getOutputs().get(0).getId(),
                    blockchain.getGenesisTransaction().getOutputs().get(0));

            for(Transaction currentTransaction: block.transactions) {

                if (currentTransaction.getTransactionId().equals(Transaction.GENESIS_TRANSACTION_HASH))
                    continue;


                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction is Invalid");
                    return false;
                }

                if(!currentTransaction.getInputsValue().equals(currentTransaction.getOutputsValue())) {
                    System.out.println("#Inputs are note equal to outputs on Transaction");
                    return false;
                }

                for(TransactionInput input: currentTransaction.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction is Missing");
                        return false;
                    }

                    if(!input.getUTXO().getValue().equals(tempOutput.getValue())) {
                        System.out.println("#Referenced input Transaction value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for(TransactionOutput output: currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if( currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.getOutputs().get(1).getRecipient() != currentTransaction.getSender()) {
                    System.out.println("#Transaction output 'change' is not sender.");
                    return false;
                }
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
