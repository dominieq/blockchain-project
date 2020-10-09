package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.Sender;

import java.security.KeyPair;

public final class SenderBuilder {

    private String name;
    private KeyPair keyPair;
    private BlockChain blockChain;

    private SenderBuilder() { }

    public static SenderBuilder builder() {
        return new SenderBuilder();
    }

    public SenderBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SenderBuilder withKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        return this;
    }

    public SenderBuilder withBlockChain(BlockChain blockChain) {
        this.blockChain = blockChain;
        return this;
    }

    public Sender build() {
        return new Sender(name, keyPair, blockChain);
    }
}
