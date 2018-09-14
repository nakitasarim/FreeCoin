package com.hilspot;

import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by one on 31.08.2018.
 */
public class NakiChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Cüzdanlar
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        System.out.println("#ilk Transaction ile walletA'ya 100 coin üretilip yükleniyor:");
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0"; //set transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Genesis block (ilk blok) olusturuluyor... ");
        Block genesis = new Block("0","0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //test
        Block block1 = new Block(genesis.hash,blockchain.get(blockchain.size()-1).hash);
        System.out.println("\nWalletA bakiyesi : " + walletA.getBalance());
        System.out.println("\nWalletA dan WalletB' ye (40) coin...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA bakiyesi: " + walletA.getBalance());
        System.out.println("WalletB bakiyesi " + walletB.getBalance());

        Block block2 = new Block(block1.hash,blockchain.get(blockchain.size()-1).hash);
        System.out.println("\nWalletA dan (1000) coin yollanmasi...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA bakiyesi: " + walletA.getBalance());
        System.out.println("WalletB bakiyesi: " + walletB.getBalance());

        Block block3 = new Block(block2.hash,blockchain.get(blockchain.size()-1).hash);
        System.out.println("\nWalletB dan WalletA'ya (20) coin...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nWalletA bakiyesi: " + walletA.getBalance());
        System.out.println("WalletB bakiyesi: " + walletB.getBalance());

        isChainValid();

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));


        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Aktif hashler esit degil");
                return false;
            }

            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous hashler esit degil");
                return false;
            }

            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#Bu blok cozulemedi");
                return false;
            }


            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifiySignature()) {
                    System.out.println("#imza (" + t + ") onaysız");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Girdiler (" + t + ") işlemldi çiktilar eşittir.");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referans girdi (" + t + ") kayıp.");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referans girdi (" + t + ") değeri geçersiz.");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") alıcı hatalı.");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") gönderici hatalı.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain geçerli");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}

