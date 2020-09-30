package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.blocks.*;
import org.example.blockchain.logic.messages.Message;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;

public class Miner extends User {

    public Miner(String name, KeyPair keyPair, BlockChain blockChain) {
        super(name, keyPair, blockChain);
    }

    @Override
    public void run() {
        try {
            boolean finished = false;

            while (!finished) {
                finished = this.mineBlock();
            }

            this.coins = 100L;
            while (this.coins > 0) {
                this.sendTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected synchronized void sendTransaction() {
        this.coins--;
    }

    private boolean mineBlock() {
        Block prevBlock = this.blockChain.getLast();

        final long timestamp = new Date().getTime();
        final long createdBy = Thread.currentThread().getId();
        final ArrayList<Message> messages = this.blockChain.getMessages();

        long id = 1L;
        String previousHash = "0";
        int nProgress = 0;

        if (prevBlock != null) {
            id = prevBlock.getId() + 1L;
            previousHash = prevBlock.getHash();
            nProgress = prevBlock.getNProgress();
        }

        final long start = System.currentTimeMillis();

        int magicNumber = Blocks.findMagicNumber(
                nProgress, id + timestamp + previousHash + createdBy
        );

        final long end = System.currentTimeMillis();
        final long generationTime = (end - start) / 1000L;

        String hash = Blocks.applySha256(id + timestamp + previousHash + createdBy + magicNumber);

        Block block = new BlockBuilder()
                .withId(id)
                .withTimestamp(timestamp)
                .withMagicNumber(magicNumber)
                .withGenerationTime(generationTime)
                .withHash(hash)
                .withPreviousHash(previousHash)
                .withCreatedBy(createdBy)
                .withNProgress(nProgress)
                .withMessages(prevBlock != null ? messages : new ArrayList<>())
                .build();

        return this.blockChain.putLast(block, generationTime);
    }

    @Override
    protected String getName() {
        return this.name;
    }

    @Override
    public long getCoins() {
        return this.coins;
    }

    @Override
    public void setCoins(long coins) {
        this.coins = coins;
    }

    @Override
    public BlockChain getBlockChain() {
        return blockChain;
    }

    @Override
    protected KeyPair getKeyPair() {
        return this.keyPair;
    }
}
