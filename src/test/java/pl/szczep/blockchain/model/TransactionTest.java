package pl.szczep.blockchain.model;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.szczep.blockchain.personal.Wallet;
import pl.szczep.blockchain.util.KeysHelper;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.Security;

import static org.assertj.core.api.Assertions.assertThat;


public class TransactionTest {

    private static Wallet sender;
    private static Wallet recipient;

    @BeforeClass
    public static void setUp() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        sender = new Wallet();
        recipient = new Wallet();
    }

    @Test
    public void shouldSingTransaction() {
        final Transaction transaction = Transaction.builder()
                .from(sender.getPublicKey())
                .to(recipient.getPublicKey())
                .value(BigDecimal.TEN)
                .build();

        transaction.generateSignature(sender.getPrivateKey());

        assertThat(transaction.verifiySignature()).isTrue();
    }

    @Test
    public void shouldTransactionBeInvalidForDataManipulation() throws IllegalAccessException {
        final Transaction transaction = Transaction.builder()
                .from(sender.getPublicKey())
                .to(recipient.getPublicKey())
                .value(BigDecimal.TEN)
                .build();

        transaction.generateSignature(sender.getPrivateKey());

        KeyPair otherRecipient = KeysHelper.generateKeyPair();
        FieldUtils.writeField(transaction, "recipient",
                otherRecipient.getPublic(), true);

        assertThat(transaction.verifiySignature()).isFalse();
    }
}
