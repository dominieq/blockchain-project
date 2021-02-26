package org.example.blockchain.simulation.administrator.builder;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.simulation.Simulation;
import org.example.blockchain.simulation.administrator.Administrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AdministratorBuilderTest {

    private AdministratorBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = AdministratorBuilder.builder();
    }

    @Test
    public void should_build_valid_administrator() {

        // given
        final Simulation simulation = mock(Simulation.class);
        final BlockChain blockchain = mock(BlockChain.class);
        doReturn(5).when(blockchain).size();

        // when
        final Administrator actual = subject
                .in(simulation)
                .manage(blockchain)
                .build();

        // then
        assertThat(actual)
                .hasFieldOrPropertyWithValue("simulation", simulation)
                .hasFieldOrPropertyWithValue("blockchain", blockchain)
                .hasFieldOrPropertyWithValue("prevBlockchainSize", 5)
                .hasFieldOrPropertyWithValue("stagnancy", 0);

        verify(blockchain, times(1)).size();
    }
}
