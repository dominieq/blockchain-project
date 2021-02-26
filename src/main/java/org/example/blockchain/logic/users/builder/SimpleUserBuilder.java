package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.SimpleUser;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;

/**
 * A builder for the {@link SimpleUser} class.
 *
 * @author Dominik Szmyt
 * @see SimpleUser
 * @since 1.0.0
 */
public final class SimpleUserBuilder {

    private Long id;
    private String name;
    private KeyPair keyPair;
    private BlockChain blockChain;
    private Simulation simulation;

    private SimpleUserBuilder() { }

    public static SimpleUserBuilder builder() {
        return new SimpleUserBuilder();
    }

    public SimpleUserBuilder withId(final Long id) {
        this.id = id;
        return this;
    }

    public SimpleUserBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public SimpleUserBuilder withKeyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;
        return this;
    }

    public SimpleUserBuilder withBlockChain(final BlockChain blockChain) {
        this.blockChain = blockChain;
        return this;
    }

    public SimpleUserBuilder withSimulation(final Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    public SimpleUser build() {
        return new SimpleUser(id, name, keyPair, blockChain, simulation);
    }
}
