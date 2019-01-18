package pl.szczep.blockchain.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;

public class DigitalSignatureTest {

    @Test
    public void shouldCalculateHexSha256() {
        assertThat(DigitalSignature.applySha256("Hello Blockchain"))
            .isEqualTo("7cf88f2ee398c0b7c0e760a1dccaf3571e0baccf310f11fe3bdfd0b09675ea75");
    }
}