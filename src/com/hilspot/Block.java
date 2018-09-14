package com.hilspot;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by one on 31.08.2018.
 */
public class Block {

    public String hash;
    public String previousHash;
    private String data;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private long timeStamp;
    private int nonce;


    public Block(String data,String previousHash ) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
        return calculatedhash;
    }


    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Blok bulundu! : " + hash);
    }


    public boolean addTransaction(Transaction transaction) {

        if(transaction == null) return false;
        if((previousHash != "0")) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction işlenemedi.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction işledi, bloğa eklendi.");
        return true;
    }
}
