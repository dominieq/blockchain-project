package org.example.blockchain.logic.users;

import io.vavr.Tuple;
import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class MinerTest {

    private BlockChain blockchain;
    private Miner subject;

    @BeforeEach
    public void setUp() {
        blockchain = mock(BlockChain.class);

        subject = MinerBuilder.builder()
                .withId(1L)
                .withName("TestMiner")
                .withKeyPair(mock(KeyPair.class))
                .withBlockChain(blockchain)
                .withSimulation(mock(Simulation.class))
                .build();
    }

    @Test
    public void should_execute_mine_without_repetitions() {

        // given
        when(blockchain.getLastWithNumberOfZeros()).thenReturn(Tuple.of(null, 0));
        when(blockchain.getCurrentMessages()).thenReturn(Collections.emptyList());
        when(blockchain.putLastAndDisplay(any(Block.class), anyLong())).thenReturn(true);

        // when
        final Block actual = subject.executeMine();

        // then
        assertThat(actual.getId()).isOne();
        assertThat(actual.getPreviousHash()).isEqualTo("0");
        assertThat(actual.getCreatedBy()).isEqualTo(subject.getId());
        assertThat(actual.getMessages()).isEmpty();
        assertThat(actual.getNProgress()).isZero(); // Blockchain is mocked so n progress wasn't changed.
        verify(blockchain, times(1)).getLastWithNumberOfZeros();
        verify(blockchain, times(1)).getCurrentMessages();
        verify(blockchain, times(1)).putLastAndDisplay(any(Block.class), anyLong());
    }
}
