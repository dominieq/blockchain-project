package org.example.blockchain.logic.messages;

import java.security.*;

public final class Messages {

    public static boolean verify(String data, byte[] signature, PublicKey key) {
        try {
            Signature sig = Signature.getInstance("SHA256withDSA");
            sig.initVerify(key);
            sig.update(data.getBytes());
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public static byte[] sign(String data, PrivateKey key) {
        try {
            Signature sig = Signature.getInstance("SHA256withDSA");
            sig.initSign(key);
            sig.update(data.getBytes());
            return sig.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException exception) {
            exception.printStackTrace();
        }

        return new byte[0];
    }
}
