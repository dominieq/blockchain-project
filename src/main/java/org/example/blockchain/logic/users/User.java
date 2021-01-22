package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.Random;

public class User extends AbstractUser {

    private volatile boolean active = true;
    private volatile boolean terminated = false;

    public User(final String name,
                final KeyPair keyPair,
                final BlockChain blockChain,
                final Simulation simulation) {

        super(name, keyPair, blockChain, simulation);
    }

    @Override
    public void run() {
        while (active) {
            try {
                boolean finished = false;

                while (!finished) {
                    finished = blockChain.addMessage(prepareMessage());
                }

                addCoins(new Random().nextInt(100) + 1);
                sleep();
                if (!active) break;

                simulation.createAndPerformTransaction(this);
            } catch (InterruptedException exception) {
                active = false;
            }
        }

        terminated = true;
    }

    @Override
    public void terminate() {
        active = false;
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
    boolean isActive() {
        return active;
    }

    @Override
    boolean isTerminated() {
        return terminated;
    }
}
