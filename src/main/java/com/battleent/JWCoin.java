package com.battleent;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class JWCoin {

    private static ArrayList<Block> blockchain = new ArrayList<>();
    private static int difficulty = 5;

    /**
     * Jw-Coin main process
     *
     * @param args none
     */
    public static void main(String[] args) {
        System.out.println("start JW-Coin mining.");

        blockchain.add(new Block("HI this is a first coin!", "0"));
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("this is second coin", blockchain.get(0).hash));
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("this is third coin", blockchain.get(1).hash));
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("check BlockChain validation : " + checkChainValidation());

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\n====== Coins ======");
        System.out.println(blockChainJson);
    }

    /**
     * check block is valid or not
     *
     * @return block validation
     */
    public synchronized static Boolean checkChainValidation() {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.getBlockHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }
}