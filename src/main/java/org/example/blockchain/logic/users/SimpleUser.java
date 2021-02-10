package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.Random;

/**
 * Represents a simple blockchain user who is only going to perform transactions.
 * After each performed transaction a user earns random amount of coins what simulates using real life savings.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class SimpleUser extends AbstractUser {

    private volatile boolean active = true;
    private volatile boolean terminated = false;

    /**
     * Create a {@code SimpleUser} with all necessary fields.
     * @param name The name of a {@code SimpleUser}.
     * @param keyPair The key pair used to sign {@code SecureMessages}.
     * @param blockChain An instance of the {@link BlockChain}.
     * @param simulation An instance of the {@link Simulation}.
     */
    public SimpleUser(final String name,
                      final KeyPair keyPair,
                      final BlockChain blockChain,
                      final Simulation simulation) {

        super(name, keyPair, blockChain, simulation);
    }

    @Override
    public void run() {
        while (active) {
            simulation.createAndPerformTransaction(this);
            addCoins(new Random().nextInt(100) + 1);

            sleep();
            if (!active) break;
        }

        terminated = true;
    }

    /**
     * Stops simple user's thread by exiting its {@code while} loop and stopping any other thread.
     */
    @Override
    public void terminate() {
        active = false;
        sleepExecutor.shutdownNow();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCoins() {
        return coins;
    }

    @Override
    public KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    public BlockChain getBlockChain() {
        return blockChain;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }
}
