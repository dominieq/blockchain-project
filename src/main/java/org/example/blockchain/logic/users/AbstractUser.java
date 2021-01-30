package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.builder.SecureMessageBuilder;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Represents a real life blockchain user who can perform transactions that may be included in one of the blocks.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public abstract class AbstractUser implements Runnable {

    protected final String name;
    protected volatile int coins;
    protected final KeyPair keyPair;
    protected final BlockChain blockChain;
    protected final Simulation simulation;

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
     * Sleeps for the random amount of time.
     * @throws InterruptedException When any thread interrupted current thread while current thread was sleeping.
     */
    protected void sleep() throws InterruptedException {
        TimeUnit.SECONDS.sleep(new Random().nextInt(15) + 1);
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

    abstract boolean isActive();

    abstract boolean isTerminated();
}
