package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.Sender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class SenderBuilderTest {

    private static KeyPairGenerator generator;
    private SenderBuilder subject;

    @BeforeAll
    public static void initialize() throws NoSuchAlgorithmException {
        generator = KeyPairGenerator.getInstance("DSA");
        generator.initialize(2048);
    }

    @BeforeEach
    public void setUp() {
        subject = SenderBuilder.builder();
    }

    @Test
    public void should_build_valid_sender() {

        // given
        final String name = "TestSender";
        final KeyPair keyPair = generator.generateKeyPair();
        final BlockChain blockChain = BlockChain.getInstance();

        // when
        final Sender actual = subject
                .withName(name)
                .withKeyPair(keyPair)
                .withBlockChain(blockChain)
                .build();

        // then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("coins", 100L)
                .hasFieldOrPropertyWithValue("keyPair", keyPair)
                .hasFieldOrPropertyWithValue("blockChain", blockChain);
    }
}
