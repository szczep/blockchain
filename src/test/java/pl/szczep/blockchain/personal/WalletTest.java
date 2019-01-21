package pl.szczep.blockchain.personal;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.security.Security;
import java.util.ArrayList;

import org.junit.Test;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;
import pl.szczep.blockchain.model.Transaction;
import pl.szczep.blockchain.model.TransactionOutput;
import pl.szczep.blockchain.util.BlockchainValidator;

public class WalletTest {

    @Test
    public void shouldProperlySendFunds() {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Blockchain blockchain = Blockchain.builder().blocks(new ArrayList<>()).build();

        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        Transaction genesisTransaction = Transaction.builder()
            .from(coinbase.getPublicKey())
            .to(walletA.getPublicKey())
            .value(new BigDecimal(100))
            .inputs(new ArrayList<>())
            .build();
        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        genesisTransaction.setTransactionId("0");


        final TransactionOutput transactionOutput = TransactionOutput.builder()
            .recipient(genesisTransaction.getRecipient())
            .value(genesisTransaction.getValue())
            .parentTransactionId(genesisTransaction.getTransactionId())
            .build();

        genesisTransaction.getOutputs().add(transactionOutput);
        Blockchain.addNewTransaction(transactionOutput);

        Block genesis = Block.builder().previousHash(Block.GENESIS_BLOCK_PREV_HASH).build();
        genesis.addTransaction(genesisTransaction);
        blockchain.addBlock(genesis);
        blockchain.setGenesisTransaction(genesisTransaction);

        Block block1 = Block.builder().previousHash(genesis.getHash()).build();
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal(40)));
        blockchain.addBlock(block1);

        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = Block.builder().previousHash(block1.getHash()).build();
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal(1000)));
        blockchain.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = Block.builder().previousHash(block2.getHash()).build();
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(),  new BigDecimal(20)));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        assertThat(BlockchainValidator.validate(blockchain)).isTrue();
    }
}