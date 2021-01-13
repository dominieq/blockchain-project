package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;

import java.security.KeyPair;

public abstract class User implements Runnable {

    protected final String name;
    protected volatile long coins;
    protected final KeyPair keyPair;
    protected final BlockChain blockChain;

    public User(final String name1,
                final KeyPair keyPair1,
                final BlockChain blockChain1) {

        this.name = name1;
        this.coins = 100L;
        this.keyPair = keyPair1;
        this.blockChain = blockChain1;
    }

    @Override
    public String toString() {
        return name;
    }

    abstract protected void sendTransaction();

    abstract protected String getName();

    abstract protected long getCoins();

    abstract protected void setCoins(final long coins);

    abstract protected KeyPair getKeyPair();

    abstract protected BlockChain getBlockChain();
}
