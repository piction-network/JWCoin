package com.battleent;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class JWCoin {

    private static ArrayList<Block> blockchain = new ArrayList<>();
    private static ArrayList<Wallet> wallets = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>(); //list of all unspent transactions.

    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;

    private static Transaction transaction;

    /**
     * Jw-Coin main process
     *
     * @param args none
     */
    public static void main(String[] args) {
        //add our blocks to the blockchain ArrayList:
        Security.addProvider(new BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider

        wallets.add(new Wallet());
        wallets.add(new Wallet());
        Wallet coinWallet = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        transaction = new Transaction(coinWallet.publicKey, wallets.get(0).publicKey, 100f, null);
        transaction.generateSignature(coinWallet.privateKey);	 //manually sign the genesis transaction
        transaction.transactionId = "0"; //manually set the transaction id
        transaction.outputs.add(new TransactionOutput(transaction.reciepient, transaction.value, transaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(transaction.outputs.get(0).id, transaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block coinA = new Block("This is a coin 1","0");
        coinA.addTransaction(transaction);
        addBlock(coinA);

        //testing
        Block coinB = new Block("This is a coin 2", coinA.hash);
        System.out.println("\nWalletA's balance is: " + wallets.get(0).getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        coinB.addTransaction(wallets.get(0).sendFunds(wallets.get(1).publicKey, 40f));
        addBlock(coinB);
        System.out.println("\nWalletA's balance is: " + wallets.get(0).getBalance());
        System.out.println("WalletB's balance is: " + wallets.get(1).getBalance());

        Block block2 = new Block("This is a coin 3", coinB.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(wallets.get(0).sendFunds(wallets.get(1).publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + wallets.get(0).getBalance());
        System.out.println("WalletB's balance is: " + wallets.get(1).getBalance());

        /**
         * check blocks validations
         */
        checkChainValidation();
    }

    /**
     * add a block in chain list

     * @param newBlock a new block
     */
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    /**
     * check block is valid or not
     *
     * @return block validation
     */
    private static Boolean checkChainValidation() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(transaction.outputs.get(0).id, transaction.outputs.get(0));

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.getBlockHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }

        System.out.println("\nBlockchain is valid");
        return true;
    }
}