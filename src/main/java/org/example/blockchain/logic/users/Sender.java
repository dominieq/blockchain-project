package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.*;

import java.security.KeyPair;

public class Sender extends User {

    public Sender(final String name,
                  final KeyPair keyPair,
                  final BlockChain blockChain) {

        super(name, keyPair, blockChain);
    }

    @Override
    public void run() {
        try {
            boolean finished = false;

            while (!finished) {
                final String text = name + ": Hello there!";
                final int id = blockChain.getUniqueIdentifier();
                final byte[] signature = Messages.sign(text + id, keyPair.getPrivate());
                final SecureMessage message = new SecureMessage(text, id, signature, keyPair.getPublic());

                finished = blockChain.addMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendTransaction() {

    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected long getCoins() {
        return coins;
    }

    @Override
    protected void setCoins(final long coins) {
        this.coins = coins;
    }

    @Override
    protected KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    protected BlockChain getBlockChain() {
        return blockChain;
    }
}
