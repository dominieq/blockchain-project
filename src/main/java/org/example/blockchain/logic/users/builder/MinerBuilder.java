package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.Miner;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;

/**
 * A builder for the {@link Miner} class.
 *
 * @author Dominik Szmyt
 * @see Miner
 * @since 1.0.0
 */
public final class MinerBuilder {

    private Long id;
    private String name;
    private KeyPair keyPair;
    private BlockChain blockChain;
    private Simulation simulation;

    private MinerBuilder() { }

    public static MinerBuilder builder() {
        return new MinerBuilder();
    }

    public MinerBuilder withId(final Long id) {
        this.id = id;
        return this;
    }

    public MinerBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public MinerBuilder withKeyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;
        return this;
    }

    public MinerBuilder withBlockChain(final BlockChain blockChain) {
        this.blockChain = blockChain;
        return this;
    }

    public MinerBuilder withSimulation(final Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    public Miner build() {
        return new Miner(id, name, keyPair, blockChain, simulation);
    }
}
