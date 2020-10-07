package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.*;

import java.security.KeyPair;

public class Sender extends User {

    public Sender(String name, KeyPair keyPair, BlockChain blockChain) {
        super(name, keyPair, blockChain);
    }

    @Override
    public void run() {
        try {
            boolean finished = false;

            while (!finished) {
                String text = this.name + ": Hello there!";
                int id = BlockChain.getUniqueIdentifier();
                byte[] signature = Messages.sign(text + id, this.keyPair.getPrivate());
                SecureMessage message = new SecureMessage(text, id, signature, this.keyPair.getPublic());

                finished = this.blockChain.addMessage(message);
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
        return this.name;
    }

    @Override
    protected long getCoins() {
        return this.coins;
    }

    @Override
    protected void setCoins(long coins) {
        this.coins = coins;
    }

    @Override
    protected KeyPair getKeyPair() {
        return this.keyPair;
    }

    @Override
    protected BlockChain getBlockChain() {
        return this.blockChain;
    }
}
