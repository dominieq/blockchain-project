package org.example.blockchain.logic.messages;

import java.security.*;

public class SecureMessage implements Message {

    private final String text;
    private final int id;
    private final byte[] signature;
    private final PublicKey publicKey;

    public SecureMessage(String text, int id, byte[] signature, PublicKey publicKey) throws IllegalArgumentException {
        if (!Messages.verify(text + id, signature, publicKey)) {
            throw new IllegalArgumentException("Failed signature verification");
        }

        this.text = text;
        this.id = id;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SecureMessage)) {
            return false;
        }

        return this.id == ((SecureMessage) obj).getId();
    }

    @Override
    public String toString() {
        return "Identifier: " + id + "Content: " + text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getId() {
        return id;
    }

    public byte[] getSignature() {
        return signature;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
