package pl.szczep.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TransactionInput {

    private String transactionOutputId;
    private TransactionOutput UTXO;
}
