package pl.szczep.blockchain.personal;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.security.Security;
import java.util.ArrayList;

import org.junit.Test;
import pl.szczep.blockchain.model.Block;
import pl.szczep.blockchain.model.Blockchain;
import pl.szczep.blockchain.model.Transaction;
import pl.szczep.blockchain.util.BlockchainValidator;
import pl.szczep.blockchain.util.TestUtil;

public class WalletTest {

    @Test
    public void shouldProperlySendFunds() {

        Blockchain.UTXOs.clear();

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Blockchain blockchain = Blockchain.builder().blocks(new ArrayList<>()).build();

        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        Transaction genesisTransaction = TestUtil.createGenesisTransaction(walletA);

        Block genesis = Block.builder().previousHash(Block.GENESIS_BLOCK_PREV_HASH).build();
        genesis.addTransaction(genesisTransaction);
        blockchain.addBlock(genesis);
        blockchain.setGenesisTransaction(genesisTransaction);

        assertThat(walletA.getBalance()).isEqualTo(new BigDecimal(100));
        assertThat(walletB.getBalance()).isEqualTo(BigDecimal.ZERO);

        Block block1 = Block.builder().previousHash(genesis.getHash()).build();
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal(40)));
        blockchain.addBlock(block1);

        assertThat(walletA.getBalance()).isEqualTo(new BigDecimal(60));
        assertThat(walletB.getBalance()).isEqualTo(new BigDecimal(40));
        assertThat(BlockchainValidator.validate(blockchain)).isTrue();

        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = Block.builder().previousHash(block1.getHash()).build();
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal(1000)));
        blockchain.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        assertThat(walletA.getBalance()).isEqualTo(new BigDecimal(60));
        assertThat(walletB.getBalance()).isEqualTo(new BigDecimal(40));
        assertThat(BlockchainValidator.validate(blockchain)).isTrue();

        Block block3 = Block.builder().previousHash(block2.getHash()).build();
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(),  new BigDecimal(20)));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        assertThat(walletA.getBalance()).isEqualTo(new BigDecimal(80));
        assertThat(walletB.getBalance()).isEqualTo(new BigDecimal(20));
        
        assertThat(BlockchainValidator.validate(blockchain)).isTrue();
    }
}