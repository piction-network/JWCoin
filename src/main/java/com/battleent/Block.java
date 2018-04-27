package com.battleent;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = getBlockHash();
    }

    /**
     * mining block algorithm
     *
     * @param difficulty mining difficulty
     */
    public void mineBlock(int difficulty) {
        synchronized (this) {
            String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
            while (!hash.substring(0, difficulty).equals(target)) {
                nonce++;
                hash = getBlockHash();
            }
            System.out.println("Block Mined!!! : " + hash);
        }
    }

    /**
     * get block hashcode based on SHA-256 // string format UTF-8
     *
     * @return block data hash
     */
    public String getBlockHash() {
        return Sha256.hash(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
    }
}