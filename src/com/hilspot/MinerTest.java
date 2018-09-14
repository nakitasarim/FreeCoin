package com.hilspot;

import com.google.gson.GsonBuilder;

import java.security.Security;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by one on 12.09.2018.
 */
public class MinerTest {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //Harcanmamış transactionların listesi
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {

        testBlockChain();
        //testWallet();
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');


        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Aktif blok ile hashler eşit değil.");
                return false;
            }

            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Önceki blok hashleri eşit değil.");
                return false;
            }

            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("Bu blok bulunamadı");
                return false;
            }
        }
        return true;
    }

    private static void testBlockChain(){

        System.out.println("Miner Testi, Zorluk Derecesi :"+difficulty+ " Tarih saat :"+ Date.from(Instant.now()));
        blockchain.add(new Block("Merhaba ben ilk blok", "0"));
        System.out.println("1. blok bulunuyor... ");
        blockchain.get(0).mineBlock(difficulty);
        System.out.println(" Tarih saat :"+ Date.from(Instant.now()));
        blockchain.add(new Block("Bu ikinci bloktur",blockchain.get(blockchain.size()-1).hash));
        System.out.println("2. blok bulunuyor... ");
        blockchain.get(1).mineBlock(difficulty);
        System.out.println(" Tarih saat :"+ Date.from(Instant.now()));
        blockchain.add(new Block("Ben de üçüncü blok",blockchain.get(blockchain.size()-1).hash));
        System.out.println("3. blok bulunuyor... ");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlok zinciri durumu: " + isChainValid() +" Tarih saat :"+ Date.from(Instant.now()));
        System.out.println(" Tarih saat :"+ Date.from(Instant.now()));
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nBlok Zinciri: ");
        System.out.println(blockchainJson);
    }

    private static void testWallet(){

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        System.out.println("CÜZDAN Testi:");
        System.out.println("Özel ve genel Anahtarlar:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        System.out.println("İmza onaylandı.");
        System.out.println(transaction.verifiySignature());
    }
}
