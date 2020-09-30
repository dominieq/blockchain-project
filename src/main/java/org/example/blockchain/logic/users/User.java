package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;

import java.security.KeyPair;

public abstract class User implements Runnable {

    protected final String name;
    protected volatile long coins;
    protected final KeyPair keyPair;
    protected final BlockChain blockChain;

    public User(String name, KeyPair keyPair, BlockChain blockChain) {
        this.name = name;
        this.coins = 100L;
        this.keyPair = keyPair;
        this.blockChain = blockChain;
    }

    @Override
    public String toString() {
        return this.name;
    }

    abstract protected void sendTransaction();

    abstract protected String getName();

    abstract protected long getCoins();

    abstract protected void setCoins(long coins);

    abstract protected KeyPair getKeyPair();

    abstract protected BlockChain getBlockChain();
}
