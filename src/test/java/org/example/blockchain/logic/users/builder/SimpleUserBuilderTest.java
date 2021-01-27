package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.SimpleUser;
import org.example.blockchain.simulation.Simulation;
import org.example.blockchain.simulation.builder.SimulationBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleUserBuilderTest {

    private static KeyPairGenerator generator;
    private SimpleUserBuilder subject;

    @BeforeAll
    public static void initialize() throws NoSuchAlgorithmException {
        generator = KeyPairGenerator.getInstance("DSA");
        generator.initialize(2048);
    }

    @BeforeEach
    public void setUp() {
        subject = SimpleUserBuilder.builder();
    }

    @Test
    public void should_build_valid_sender() {

        // given
        final String name = "TestUser";
        final KeyPair keyPair = generator.generateKeyPair();
        final BlockChain blockChain = BlockChain.getInstance();
        final Simulation simulation = SimulationBuilder.builder().build();

        // when
        final SimpleUser actual = subject
                .withName(name)
                .withKeyPair(keyPair)
                .withBlockChain(blockChain)
                .withSimulation(simulation)
                .build();

        // then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("coins", 100)
                .hasFieldOrPropertyWithValue("keyPair", keyPair)
                .hasFieldOrPropertyWithValue("blockChain", blockChain)
                .hasFieldOrPropertyWithValue("simulation", simulation);
    }
}
