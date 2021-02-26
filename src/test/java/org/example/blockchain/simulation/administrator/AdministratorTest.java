package org.example.blockchain.simulation.administrator;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.AbstractUser;
import org.example.blockchain.logic.users.Miner;
import org.example.blockchain.simulation.Simulation;
import org.example.blockchain.simulation.administrator.builder.AdministratorBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AdministratorTest {

    private final Simulation simulation = mock(Simulation.class);
    private BlockChain blockchain;
    private Administrator subject;

    @BeforeEach
    public void setUp() throws Exception {
        final Field field = BlockChain.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);

        blockchain = spy(BlockChain.getInstance());
        doReturn(0).when(blockchain).size();

        subject = AdministratorBuilder.builder()
                .in(simulation)
                .manage(blockchain)
                .build();
    }

    @Test
    public void should_stop_miners_and_reset_number_of_zeros_when_no_progress() {

        // given
        final Miner miner1 = mock(Miner.class);
        final Miner miner2 = mock(Miner.class);
        final List<AbstractUser> users = Arrays.asList(miner1, miner2);

        doReturn(users).when(simulation).getCurrentUsers();

        // when
        subject.run();

        // then
        verify(simulation, times(1)).getCurrentUsers();
        verify(miner1, times(1)).stopMining();
        verify(miner2, times(1)).stopMining();
        verify(blockchain, atLeastOnce()).size();
        verify(blockchain, times(1)).setNumberOfZeros(0);
        assertThat(subject.getPrevBlockchainSize()).isZero();
        assertThat(blockchain.getNumberOfZeros()).isZero();
    }

    @Test
    public void should_increase_stagnancy_when_growth_speed_too_small() {

        // given
        doReturn(1).when(blockchain).size();

        // when
        subject.run();

        // then
        verify(blockchain, atLeastOnce()).size();
        verifyNoMoreInteractions(blockchain);
        verifyNoInteractions(simulation);
        assertThat(subject.getStagnancy()).isOne();
        assertThat(subject.getPrevBlockchainSize()).isOne();
    }

    @Test
    public void should_decrease_number_of_zeros_by_half_stagnancy_reaches_threshold()
            throws Exception{

        // given
        doReturn(1).when(blockchain).size();

        final Field field = Administrator.class.getDeclaredField("stagnancy");
        field.setAccessible(true);
        field.set(subject, 4);

        // when
        subject.run();

        // then
        verify(blockchain, atLeastOnce()).size();
        verify(blockchain, times(1)).getNumberOfZeros();
        verify(blockchain, times(1)).setNumberOfZeros(0);
        verifyNoMoreInteractions(blockchain);
        verifyNoInteractions(simulation);
        assertThat(subject.getStagnancy()).isEqualTo(5);
        assertThat(subject.getPrevBlockchainSize()).isOne();
    }
}
