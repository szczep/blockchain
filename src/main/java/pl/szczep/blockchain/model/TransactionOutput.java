package pl.szczep.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.szczep.blockchain.util.DigitalSignature;
import pl.szczep.blockchain.util.KeysHelper;

import java.math.BigDecimal;
import java.security.PublicKey;

@Builder
@AllArgsConstructor
public class TransactionOutput {

    @Getter
    private String id;
    @Getter
    private PublicKey recipient;
    @Getter
    private BigDecimal value;
    private String parentTransactionId;

    public TransactionOutput(PublicKey recipient,
                             BigDecimal value,
                             String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = DigitalSignature.applySha256(
                KeysHelper.getStringFromKey(recipient)
                        + value.toString() + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}