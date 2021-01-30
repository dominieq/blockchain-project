package org.example.blockchain.logic.message;

import java.security.*;

/**
 * Contains methods for signing secure messages and verifying them.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public final class Messages {

    /**
     * Verifies if the provided signature is a valid signature for the input text using provided public key.
     *
     * @param data An input text that could have been signed.
     * @param signature A signature that could have been generated from the data.
     * @param key A public that is to be used to verify the validity of a signature.
     * @return {@code true} if a signature is valid, otherwise {@code false}.
     */
    public static boolean verify(final String data, final byte[] signature, final PublicKey key) {
        try {
            final Signature sig = Signature.getInstance("SHA256withDSA");
            sig.initVerify(key);
            sig.update(data.getBytes());
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    /**
     * Signs an input text with provided private key using SHA256 with DSA algorithm
     * and then returns the generated signature.
     *
     * @param data An input text that is to be signed.
     * @param key A private key that is to be used for signing the data.
     * @return A signature that is the result of signing an input text with provided private key.
     */
    public static byte[] sign(final String data, final PrivateKey key) {
        try {
            final Signature sig = Signature.getInstance("SHA256withDSA");
            sig.initSign(key);
            sig.update(data.getBytes());
            return sig.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException exception) {
            exception.printStackTrace();
        }

        return new byte[0];
    }
}
