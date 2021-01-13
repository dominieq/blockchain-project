package org.example.blockchain.logic.message;

import java.security.*;

public final class Messages {

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
