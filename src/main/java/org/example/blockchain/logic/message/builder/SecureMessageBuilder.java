package org.example.blockchain.logic.message.builder;

import org.example.blockchain.logic.message.SecureMessage;

import java.security.PublicKey;

import static java.util.Objects.isNull;

public final class SecureMessageBuilder {

    private String text;
    private int id;
    private byte[] signature;
    private PublicKey publicKey;

    private SecureMessageBuilder() { }

    public static SecureMessageBuilder builder() {
        return new SecureMessageBuilder();
    }

    public SecureMessageBuilder withText(final String text) {
        this.text = text;
        return this;
    }

    public SecureMessageBuilder withId(final int id) {
        this.id = id;
        return this;
    }

    public SecureMessageBuilder withSignature(final byte[] signature) {
        this.signature = signature;
        return this;
    }

    public SecureMessageBuilder withPublicKey(final PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public SecureMessage build() {
        return new SecureMessage(text, id, signature, publicKey);
    }
}
