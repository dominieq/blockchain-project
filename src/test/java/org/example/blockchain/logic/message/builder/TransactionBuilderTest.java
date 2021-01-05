package org.example.blockchain.logic.message.builder;

import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.Transaction;
import org.example.blockchain.logic.users.User;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.logic.users.builder.SenderBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionBuilderTest {

    private static KeyPairGenerator generator;
    private TransactionBuilder subject;

    @BeforeAll
    public static void initialize() throws NoSuchAlgorithmException {
        generator = KeyPairGenerator.getInstance("DSA");
        generator.initialize(2048);
    }

    @BeforeEach
    public void setUp() {
        subject = TransactionBuilder.builder();
    }

    @Test
    public void should_build_valid_transaction() {

        // given
        final KeyPair keyPair = generator.generateKeyPair();
        final String text = "Fancy text message";
        final int id = 1;
        final byte[] signature = Messages.sign(text + id, keyPair.getPrivate());

        final Message message = SecureMessageBuilder.builder()
                .withId(id)
                .withText(text)
                .withSignature(signature)
                .withPublicKey(keyPair.getPublic())
                .build();

        final User sender = SenderBuilder.builder()
                .withName("TestSender")
                .withKeyPair(keyPair)
                .build();

        final User receiver = MinerBuilder.builder()
                .withName("TestReceiver")
                .withKeyPair(keyPair)
                .build();

        // when
        final Transaction transaction = subject
                .withMessage(message)
                .withFrom(sender)
                .withTo(receiver)
                .withAmount(100L)
                .build();

        // then
        assertThat(transaction)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", message)
                .hasFieldOrPropertyWithValue("amount", 100L);
    }
}
