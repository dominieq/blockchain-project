package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.Miner;

import java.security.KeyPair;

public final class MinerBuilder {
    private String name;
    private KeyPair keyPair;
    private BlockChain blockChain;

    private MinerBuilder() { }

    public static MinerBuilder builder() {
        return new MinerBuilder();
    }

    public MinerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MinerBuilder withKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        return this;
    }

    public MinerBuilder withBlockChain(BlockChain blockChain) {
        this.blockChain = blockChain;
        return this;
    }

    public Miner build() {
        return new Miner(name, keyPair, blockChain);
    }
}
