package pl.szczep.blockchain.util;

import org.junit.BeforeClass;
import org.junit.Test;

import java.security.KeyPair;
import java.security.Security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DigitalSignatureTest {


    private static KeyPair keyPair;

    @BeforeClass
    public static void setUp(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        keyPair = KeysHelper.generateKeyPair();
    }


    @Test
    public void shouldCalculateHexSha256() {
        assertThat(DigitalSignature.applySha256("Hello Blockchain"))
            .isEqualTo("7cf88f2ee398c0b7c0e760a1dccaf3571e0baccf310f11fe3bdfd0b09675ea75");
    }

    @Test
    public void shouldSingAndVerifyInput() {
        final String message = "Transfer from John to Sally 5$";

        byte[] signature = DigitalSignature.applyECDSASig(keyPair.getPrivate(), message);

        assertThat(DigitalSignature.verifyECDSASig(keyPair.getPublic(), message, signature))
                .isTrue();
    }

    @Test
    public void shouldDetectThatSingedDataAreCorrupted() {
        final String message = "Transfer from John to Sally 5$";
        final String hackedMessage = message.replace("Sally", "Piotr");

        byte[] signature = DigitalSignature.applyECDSASig(keyPair.getPrivate(), message);

        assertThat(DigitalSignature.verifyECDSASig(keyPair.getPublic(), hackedMessage, signature))
                .isFalse();
    }

    @Test
    public void shouldDetectThatContractIsSingedBySomeoneElse() {
        final String message = "Transfer from John to Sally 5$";
        final String hackedMessage = message.replace("Sally", "Piotr");

        byte[] signature = DigitalSignature.applyECDSASig(keyPair.getPrivate(), message);

        KeyPair otherKeys = KeysHelper.generateKeyPair();

        assertThat(DigitalSignature.verifyECDSASig(otherKeys.getPublic(), hackedMessage, signature))
                .isFalse();
    }
}
