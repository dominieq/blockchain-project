package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.SimpleUser;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SimpleUserBuilderTest {

    private SimpleUserBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = SimpleUserBuilder.builder();
    }

    @Test
    public void should_build_valid_simple_user() {

        // given
        final String name = "TestUser";
        final KeyPair keyPair = mock(KeyPair.class);
        final BlockChain blockChain = mock(BlockChain.class);
        final Simulation simulation = mock(Simulation.class);

        // when
        final SimpleUser actual = subject
                .withName(name)
                .withKeyPair(keyPair)
                .withBlockChain(blockChain)
                .withSimulation(simulation)
                .build();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getCoins()).isEqualTo(100);
        assertThat(actual)
                .hasFieldOrPropertyWithValue("keyPair", keyPair)
                .hasFieldOrPropertyWithValue("blockChain", blockChain)
                .hasFieldOrPropertyWithValue("simulation", simulation);
        assertThat(actual.isActive()).isTrue();
        assertThat(actual.isTerminated()).isFalse();
    }
}
