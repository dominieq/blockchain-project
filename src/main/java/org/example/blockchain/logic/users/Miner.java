package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.block.*;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a user who is going to mine blocks apart from performing transactions.
 * After each added block a miner earns 100 coins.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class Miner extends AbstractUser {

    private volatile boolean active = true;
    private volatile boolean terminated = false;

    public Miner(final String name,
                 final KeyPair keyPair,
                 final BlockChain blockChain,
                 final Simulation simulation) {

        super(name, keyPair, blockChain, simulation);
    }

    @Override
    public void run() {
        while (active) {
            try {
                Block block = null;
                boolean isIn = false;

                while (!isIn) {
                    final Block prevBlock = blockChain.getLast();
                    final List<Message> messages = new ArrayList<>(blockChain.getMessages());
                    block = Blocks.mineBlock(prevBlock, messages, new Date().getTime(), Thread.currentThread().getId());

                    isIn = blockChain.putLast(block);
                }

                System.out.println(block);
                addCoins(100);
                sleep();
                if (!active) break;

                simulation.createAndPerformTransaction(this);
            } catch (InterruptedException exception) {
                active = false;
            }
        }

        terminated = true;
    }

    /**
     * Stops miner thread's <tt>while</tt> loop.
     */
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
    public BlockChain getBlockChain() {
        return blockChain;
    }

    @Override
    public KeyPair getKeyPair() {
        return keyPair;
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
