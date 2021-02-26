package org.example.blockchain.logic.users.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.Miner;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MinerBuilderTest {

    private MinerBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = MinerBuilder.builder();
    }

    @Test
    public void should_build_valid_miner() {

        // given
        final String name = "TestMiner";
        final KeyPair keyPair = mock(KeyPair.class);
        final BlockChain blockChain = mock(BlockChain.class);
        final Simulation simulation = mock(Simulation.class);

        // when
        final Miner actual = subject
                .withId(1L)
                .withName(name)
                .withKeyPair(keyPair)
                .withBlockChain(blockChain)
                .withSimulation(simulation)
                .build();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isOne();
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
