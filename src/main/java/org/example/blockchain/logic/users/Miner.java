package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.block.*;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.nonNull;

/**
 * Represents a user who is going to mine blocks apart from performing transactions.
 * After each added block a miner earns 100 coins.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class Miner extends AbstractUser {

    private ExecutorService miningService;
    private volatile boolean active = true;
    private volatile boolean terminated = false;

    /**
     * Create a {@code Miner} with all necessary fields.
     * @param name The name of a {@code Miner}.
     * @param keyPair The key pair used to sign {@code SecureMessages}.
     * @param blockChain An instance of the {@link BlockChain}.
     * @param simulation An instance of the {@link Simulation}.
     */
    public Miner(final String name,
                 final KeyPair keyPair,
                 final BlockChain blockChain,
                 final Simulation simulation) {

        super(name, keyPair, blockChain, simulation);
    }

    @Override
    public void run() {
        while (active) {
            miningService = Executors.newSingleThreadExecutor();
            CompletableFuture.supplyAsync(this::mineBlock, miningService)
                    .whenComplete(((block, throwable) -> {
                        if (nonNull(block)) {
                            System.out.println(block);
                            addCoins(100);
                        } else if (nonNull(throwable)) {
                            if (active) active = false;
                        }
                    }));
            if (!active) break;

            sleep();
            if (!active) break;

            simulation.createAndPerformTransaction(this);
        }

        terminated = true;
    }

    /**
     * Simulates the process of mining a block by a miner.
     * @return A new block that was successfully added to a blockchain.
     * @since 1.1.0
     */
    private Block mineBlock() {
        Block block = null;
        boolean isIn = false;

        while (!isIn) {
            final Block prevBlock = blockChain.getLast();
            final List<Message> messages = new ArrayList<>(blockChain.getMessages());
            block = Blocks.mineBlock(prevBlock, messages, new Date().getTime(), Thread.currentThread().getId());

            isIn = blockChain.putLast(block, block.getGenerationTime());
        }

        return block;
    }

    /**
     * Stops miner's thread by exiting its {@code while} loop and stopping any other threads.
     */
    @Override
    public void terminate() {
        active = false;
        sleepExecutor.shutdownNow();
        miningService.shutdownNow();
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
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }
}
