package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.block.*;
import org.example.blockchain.logic.message.Message;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;

public class Miner extends User {

    public Miner(final String name,
                 final KeyPair keyPair,
                 final BlockChain blockChain) {

        super(name, keyPair, blockChain);
    }

    @Override
    public void run() {
        try {
            boolean finished = false;

            while (!finished) {
                final Block prevBlock = blockChain.getLast();
                final List<Message> messages = blockChain.getMessages();
                final Block block = Blocks
                        .mineBlock(prevBlock, messages, new Date().getTime(), Thread.currentThread().getId());

                finished = blockChain.putLast(block);
            }

            coins = 100L;
            while (coins > 0) {
                sendTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected synchronized void sendTransaction() {
        coins--;
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    public long getCoins() {
        return coins;
    }

    @Override
    public void setCoins(final long coins) {
        this.coins = coins;
    }

    @Override
    public BlockChain getBlockChain() {
        return blockChain;
    }

    @Override
    protected KeyPair getKeyPair() {
        return keyPair;
    }
}
