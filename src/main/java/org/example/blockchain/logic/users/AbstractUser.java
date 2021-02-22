package org.example.blockchain.logic.users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.builder.SecureMessageBuilder;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.concurrent.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
    protected CompletableFuture<Boolean> sleeping;
    protected final Object SLEEP_LOCK = new Object();

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
     * Calls a sleep method on the provided {@code timeUnit} with given {@code timeout}.
     * @param timeout The maximum time to sleep.
     * @param timeUnit The time unit of the timeout argument.
     * @return {@code true} if the sleep executed without interruptions, otherwise returns {@code false}.
     */
    protected boolean executeSleep(final long timeout, final TimeUnit timeUnit) {
        if (timeout <= 0 || isNull(timeUnit)) return false;

        try {
            LOGGER.trace("{} is sleeping for {} {}.", this, timeout, timeUnit);
            timeUnit.sleep(timeout);
            LOGGER.trace("{} slept for {} {}.", this, timeout, timeUnit);
        } catch (final InterruptedException ignored) {
            LOGGER.debug("{} internal sleep was interrupted.", this);
            return false;
        }

        return true;
    }

    /**
     * Starts sleep execution if the previous one hasn't been cancelled.
     * Uses {@link #executeSleep(long, TimeUnit)} as a supplier for {@link CompletableFuture}.
     * @param timeout The maximum time to sleep.
     * @param timeUnit The time unit of the timeout argument.
     */
    protected void sleep(final long timeout, final TimeUnit timeUnit) {
        if (timeout <= 0 || isNull(timeUnit)) return;

        synchronized (SLEEP_LOCK) {
            if (nonNull(sleeping) && sleeping.isCancelled()) {
                LOGGER.trace("Sleeping has already been cancelled. {} cannot sleep right now.", this);
                return;
            }

            sleeping = CompletableFuture.supplyAsync(() -> executeSleep(timeout, timeUnit));
        }

        try {
            sleeping.get();
        } catch (final Exception ignored) {
            LOGGER.debug("{} sleep execution was interrupted", this);
        }
    }

    /**
     * Starts sleep execution if the previous one hasn't been cancelled.
     * Uses {@link #executeSleep(long, TimeUnit)} as a supplier for {@link CompletableFuture}.
     * @param timeout The maximum time to sleep in seconds.
     * @since 1.1.0
     */
    protected void sleep(final long timeout) {
        sleep(timeout, TimeUnit.SECONDS);
    }

    /**
     * Provides a default implementation that tries to cancel sleeping.
     * All classes that extend {@code AbstractUser} should override this method
     * and set active to false.
     */
    public void terminate() {
        synchronized (SLEEP_LOCK) {
            if (isNull(sleeping)) {
                LOGGER.trace("Sleeping hasn't been performed yet. Creating cancelled result...");
                sleeping = CompletableFuture.failedFuture(new CancellationException());
            } else {
                LOGGER.trace("Sleeping has already finished or is in progress. Cancelling process...");
                sleeping.obtrudeException(new CancellationException());
            }
        }
    }

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
