package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.builder.SecureMessageBuilder;
import org.example.blockchain.simulation.Simulation;

import java.security.KeyPair;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class AbstractUser implements Runnable {

    protected final String name;
    protected volatile int coins;
    protected final KeyPair keyPair;
    protected final BlockChain blockChain;
    protected final Simulation simulation;

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

    public Message prepareMessage() {
        final String text = name + ": Hello there!";
        final int id = blockChain.getUniqueIdentifier();
        final byte[] signature = Messages.sign(text + id, keyPair.getPrivate());

        return SecureMessageBuilder.builder()
                .withId(id)
                .withText(text)
                .withSignature(signature)
                .withPublicKey(keyPair.getPublic())
                .build();
    }

    public synchronized void addCoins(final int addend) {
        if (addend < 0) return;
        coins += addend;
    }

    public synchronized void takeCoins(final int subtrahend) {
        if (subtrahend < 0) return;
        coins -= subtrahend;
    }

    protected void sleep() throws InterruptedException {
        TimeUnit.SECONDS.sleep(new Random().nextInt(15) + 1);
    }

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
