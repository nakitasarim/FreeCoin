package com.hilspot;

/**
 * Created by one on 31.08.2018.
 */
public class TransactionInput {

    public String transactionOutputId;
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
