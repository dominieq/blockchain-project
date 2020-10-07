package org.example.blockchain.logic.message;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MessagesTest {

    private static KeyPairGenerator generator;

    @BeforeAll
    public static void setUp() throws NoSuchAlgorithmException {
        generator = KeyPairGenerator.getInstance("DSA");
        generator.initialize(2048);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Simple message",
            "Fancy message",
            "Boring message",
            "Revealing message",
            "Unfortunate message"
    })
    public void should_sign_text(String text)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        // given
        final KeyPair keyPair = generator.generateKeyPair();
        final byte[] sign = Messages.sign(text, keyPair.getPrivate());

        // when
        Signature sig = Signature.getInstance("SHA256withDSA");
        sig.initVerify(keyPair.getPublic());
        sig.update(text.getBytes());

        final Boolean actual = sig.verify(sign);

        // then
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Simple message",
            "Fancy message",
            "Boring message",
            "Revealing message",
            "Unfortunate message"
    })
    public void should_verify_text(String text)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        // given
        final KeyPair keyPair = generator.generateKeyPair();

        // when
        Signature sig = Signature.getInstance("SHA256withDSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(text.getBytes());
        final byte[] sign = sig.sign();

        final Boolean actual = Messages.verify(text, sign, keyPair.getPublic());

        // then
        assertThat(actual).isTrue();
    }
}
