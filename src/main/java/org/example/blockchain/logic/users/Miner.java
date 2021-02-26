package org.example.blockchain.logic.users;

import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.block.Blocks;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Represents a user who is going to mine blocks apart from performing transactions.
 * After each added block a miner earns 100 coins.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class Miner extends AbstractUser {

    private static final Logger LOGGER = LogManager.getLogger(Miner.class);
    protected final Object MINING_LOCK = new Object();
    protected CompletableFuture<Block> mining;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicBoolean terminated = new AtomicBoolean(false);

    /**
     * Create a {@code Miner} with all necessary fields.
     * @param name The name of a {@code Miner}.
     * @param keyPair The key pair used to sign {@code SecureMessages}.
     * @param blockChain An instance of the {@link BlockChain}.
     * @param simulation An instance of the {@link Simulation}.
     */
    public Miner(final Long id,
                 final String name,
                 final KeyPair keyPair,
                 final BlockChain blockChain,
                 final Simulation simulation) {

        super(id, name, keyPair, blockChain, simulation);
    }

    @Override
    public void run() {
        while (active.get()) {
            boolean miningInProgress = true;
            while (miningInProgress) {
                synchronized (MINING_LOCK) {
                    if (nonNull(mining) && mining.isCompletedExceptionally() && !mining.isCancelled()) {
                        active.set(false);
                        break;
                    }

                    mining = CompletableFuture.supplyAsync(this::executeMine);
                }

                try {
                    mining.get();
                    addCoins(100);
                    miningInProgress = false;
                } catch (final InterruptedException | ExecutionException ignored) {
                    active.set(false);
                    miningInProgress = false;
                    LOGGER.debug("{} was interrupted while mining.", this);
                } catch (final CancellationException ignored) {
                    LOGGER.debug("{} had its mining process cancelled.", this);
                    sleep(2500L, TimeUnit.MILLISECONDS);
                }
            }
            if (!active.get()) break;

            sleep(new Random().nextInt(15) + 1);
            if (!active.get()) break;

            simulation.createAndPerformTransaction(this);
        }

        terminated.set(true);
    }

    /**
     * Shuts down mining service, to unblock infinite search for magic number.
     * @since 1.1.0
     */
    public void stopMining() {
        if (isNull(mining)) {
            LOGGER.trace("Mining hasn't been performed yet. Creating cancelled result...");
            mining = CompletableFuture.failedFuture(new CancellationException());
        } else {
            LOGGER.trace("Mining has already finished or is in progress. Interrupting process...");
            mining.obtrudeException(new CancellationException());
        }
    }

    /**
     * Simulates the process of mining a block by a miner.
     * @return A new block that was successfully added to a blockchain.
     * @since 1.1.0
     */
    protected Block executeMine() {
        Block block = null;
        boolean isIn = false;

        while (!isIn) {
            final Tuple2<Block, Integer> last = blockChain.getLastWithNumberOfZeros();
            final List<Message> messages = blockChain.getCurrentMessages();
            final long timestamp = new Date().getTime();

            block = Blocks.mineBlock(last._1, messages, last._2, timestamp, id);

            isIn = blockChain.putLastAndDisplay(block, block.getGenerationTime());
        }

        return block;
    }

    /**
     * Stops miner's thread by exiting its {@code while} loop and stopping any other threads.
     */
    @Override
    public void terminate() {
        active.set(false);
        super.terminate();

        if (isNull(mining)) {
            LOGGER.trace("Mining hasn't been performed yet. Creating interrupted result...");
            mining = CompletableFuture.failedFuture(new InterruptedException());
        } else {
            LOGGER.trace("Mining has already finished or is in progress. Interrupting process...");
            mining.obtrudeException(new InterruptedException());
        }
    }

    @Override
    public Long getId() {
        return id;
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
