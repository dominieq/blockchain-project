package org.example.blockchain.logic.message.builder;

import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.SecureMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class SecureMessageTest {

    private static KeyPairGenerator generator;
    private SecureMessageBuilder subject;

    @BeforeAll
    public static void initialize() throws NoSuchAlgorithmException {
        generator = KeyPairGenerator.getInstance("DSA");
        generator.initialize(2048);
    }

    @BeforeEach
    public void setUp() {
        subject = SecureMessageBuilder.builder();
    }

    @Test
    public void should_build_valid_secure_message() {

        // given
        final KeyPair keyPair = generator.generateKeyPair();
        final String text = "Fancy text message";
        final int id = 1;
        final byte[] sign = Messages.sign(text + id, keyPair.getPrivate());

        // when
        final SecureMessage actual = subject
                .withId(id)
                .withText(text)
                .withSignature(sign)
                .withPublicKey(keyPair.getPublic())
                .build();

        // then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("text", text)
                .hasFieldOrPropertyWithValue("signature", sign)
                .hasFieldOrPropertyWithValue("publicKey", keyPair.getPublic());
    }
}
