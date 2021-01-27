package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.User;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;

/**
 * A builder for the {@link User} class.
 *
 * @author Dominik Szmyt
 * @see User
 * @since 1.0.0
 */
public final class UserBuilder {

    private String name;
    private KeyPair keyPair;
    private BlockChain blockChain;
    private Simulation simulation;

    private UserBuilder() { }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withKeyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;
        return this;
    }

    public UserBuilder withBlockChain(final BlockChain blockChain) {
        this.blockChain = blockChain;
        return this;
    }

    public UserBuilder withSimulation(final Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    public User build() {
        return new User(name, keyPair, blockChain, simulation);
    }
}
