package org.example.blockchain.logic.message;

import java.security.*;

/**
 * A {@code Message} that had it's content signed with a private key
 * and is shipped with a public key to verify the validity of the signature.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class SecureMessage implements Message {

    private final String text;
    private final int id;
    private final byte[] signature;
    private final PublicKey publicKey;

    /**
     * Create a {@code SecureMessage} with all necessary fields.
     * @param text The text of a {@code SecureMessage}.
     * @param id The id of a {@code SecureMessage}.
     * @param signature The signature of a {@code SecureMessage}.
     * @param publicKey The public key that is to be used for verification.
     * @throws IllegalArgumentException When the signature verification with public key didn't succeed.
     */
    public SecureMessage(final String text,
                         final int id,
                         final byte[] signature,
                         final PublicKey publicKey) throws IllegalArgumentException {

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
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SecureMessage)) {
            return false;
        }

        return id == ((SecureMessage) obj).getId();
    }

    @Override
    public String toString() {
        return "Identifier: " + id + " Content: " + text;
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
