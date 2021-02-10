package org.example.blockchain.logic.users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.builder.SecureMessageBuilder;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents a real life blockchain user who can perform transactions that may be included in one of the blocks.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public abstract class AbstractUser implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(AbstractUser.class);
    protected final String name;
    protected volatile int coins;
    protected final KeyPair keyPair;
    protected final BlockChain blockChain;
    protected final Simulation simulation;
    protected ExecutorService sleepExecutor;

    /**
     * Create an {@code AbstractUser} with all necessary fields.
     * @param name1 The name of an {@code AbstractUser}.
     * @param keyPair1 The key pair used to sign {@code SecureMessages}.
     * @param blockChain1 An instance of the {@link BlockChain}.
     * @param simulation1 An instance of the {@link Simulation}.
     */
    public AbstractUser(final String name1,
                        final KeyPair keyPair1,
                        final BlockChain blockChain1,
                        final Simulation simulation1) {

        name = name1;
        coins = 100;
        keyPair = keyPair1;
        blockChain = blockChain1;
        simulation = simulation1;
    }

    /**
     * Prepares and signs a {@code SecureMessage} using user's private key.
     * @return A secure message that may be included in a transaction.
     * @see org.example.blockchain.logic.message.SecureMessage
     */
    public Message prepareMessage() {
        final String text = "Hello there!";
        final int id = blockChain.getUniqueIdentifier();
        final byte[] signature = Messages.sign(text + id, keyPair.getPrivate());

        return SecureMessageBuilder.builder()
                .withId(id)
                .withText(text)
                .withSignature(signature)
                .withPublicKey(keyPair.getPublic())
                .build();
    }

    /**
     * Increases number of coins by a given value.
     * @param addend A value that is to be added to the {@link #coins}.
     */
    public synchronized void addCoins(final int addend) {
        if (addend < 0) return;
        coins += addend;
    }

    /**
     * Decreases number of coins by a given value.
     * @param subtrahend A value that is to be subtracted from the {@link #coins}.
     */
    public synchronized void takeCoins(final int subtrahend) {
        if (subtrahend < 0) return;
        coins -= subtrahend;
    }

    /**
     * Submits a new thread that sleeps for random amount of time between 1 and 15 seconds.
     * Immediately, shuts down the executor to await its termination.
     * @since 1.1.0
     */
    private void executeSleep() {
        sleepExecutor = Executors.newSingleThreadExecutor();

        sleepExecutor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(new Random().nextInt(15) + 1);
            } catch (InterruptedException ignored) {
                LOGGER.warn("{} internal sleep was interrupted.", this);
            }
        });

        sleepExecutor.shutdown();
    }

    /**
     * Awaits the termination of a sleep execution called within {@link #executeSleep()} method.
     */
    protected void sleep() {
        executeSleep();
        boolean isTerminated = false;

        try {
            isTerminated = sleepExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            LOGGER.warn("{} sleep execution was interrupted", this);
        } finally {
            if (!isTerminated) sleepExecutor.shutdownNow();
        }
    }

    /**
     * Each {@code AbstractUser} implementation should provide a method to stop it's thread's {@code while} loop.
     */
    abstract public void terminate();

    @Override
    public String toString() {
        return name;
    }

    abstract public String getName();

    abstract public int getCoins();

    abstract public KeyPair getKeyPair();

    abstract public BlockChain getBlockChain();

    abstract public boolean isActive();

    abstract public boolean isTerminated();
}
