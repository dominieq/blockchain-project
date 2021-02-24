package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a simple blockchain user who is only going to perform transactions.
 * After each performed transaction a user earns random amount of coins what simulates using real life savings.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class SimpleUser extends AbstractUser {

    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicBoolean terminated = new AtomicBoolean(false);

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
        while (active.get()) {
            simulation.createAndPerformTransaction(this);
            addCoins(new Random().nextInt(100) + 1);

            sleep(new Random().nextInt(15) + 1);
            if (!active.get()) break;
        }

        terminated.set(true);
    }

    /**
     * Stops simple user's thread by exiting its {@code while} loop and stopping any other thread.
     */
    @Override
    public void terminate() {
        active.set(false);
        super.terminate();
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
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public boolean isTerminated() {
        return terminated.get();
    }
}
