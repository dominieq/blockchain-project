package org.example.blockchain.simulation.administrator.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.simulation.Simulation;
import org.example.blockchain.simulation.administrator.Administrator;

/**
 * A builder for the {@link Administrator} class.
 *
 * @author Dominik Szmyt
 * @see Administrator
 * @since 1.1.0
 */
public final class AdministratorBuilder {

    private Simulation simulation;
    private BlockChain blockchain;

    private AdministratorBuilder() {}

    /**
     * Returns a new instance of an {@code AdministratorBuilder}.
     * @return A new instance of an {@code AdministratorBuilder}.
     */
    public static AdministratorBuilder builder() {
        return new AdministratorBuilder();
    }

    /**
     * Set a {@link Simulation} for a new instance of an {@link Administrator}.
     * @param simulation A {@link Simulation} in which an {@code Administrator} is to operate.
     * @return A current instance of an {@code AdministratorBuilder}.
     */
    public AdministratorBuilder in(final Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    /**
     * Set a {@link BlockChain} for a new instance of an {@link Administrator}.
     * @param blockchain A {@link BlockChain} that is to be controlled by an {@code Administrator}.
     * @return A current instance of an {@code AdministratorBuilder}.
     */
    public AdministratorBuilder manage(final BlockChain blockchain) {
        this.blockchain = blockchain;
        return this;
    }

    /**
     * Build a new instance of an {@link Administrator} with current values.
     * @return A new instance of {@link Administrator}.
     */
    public Administrator build() {
        return new Administrator(simulation, blockchain);
    }
}
