package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

public class MinerIntegrationTest {

    private Miner subject;

    @BeforeEach
    public void setUp() throws Exception {
        subject = MinerBuilder.builder()
                .withName("TestMiner-1")
                .withKeyPair(mock(KeyPair.class))
                .withBlockChain(mock(BlockChain.class))
                .withSimulation(mock(Simulation.class))
                .build();
    }

    @Test
    public void should_stop_mining_when_mining_was_not_initialized() {

        // when
        subject.stopMining();

        // then
        assertThat(subject.mining).isCancelled();
    }

    @Test
    public void should_stop_mining_when_mining_was_initialized() {

        // given
        subject.mining = CompletableFuture.completedFuture(mock(Block.class));

        // when
        subject.stopMining();

        // then
        assertThat(subject.mining).isCancelled();
    }

    @Test
    public void should_stop_mining_when_user_is_mining() {

        // given
        subject.mining = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (final InterruptedException ignored) {}

            return mock(Block.class);
        });

        // when
        subject.stopMining();

        // then
        assertThat(subject.mining).isCancelled();
    }

    //########################################################//
    //                                                        //
    //                 Test 'terminate' method                //
    //                                                        //
    //########################################################//

    @Test
    public void should_terminate_when_mining_was_not_initialized() {

        // when
        subject.terminate();

        // then
        assertThat(subject.isActive()).isFalse();
        assertThat(subject.mining)
                .isNotCancelled()
                .isCompletedExceptionally();

        final Throwable actual = catchThrowable(() -> subject.mining.get());
        assertThat(actual)
                .isExactlyInstanceOf(ExecutionException.class)
                .hasCauseExactlyInstanceOf(InterruptedException.class);
    }

    @Test
    public void should_terminate_when_mining_was_initialized() {

        // given
        subject.mining = CompletableFuture.completedFuture(mock(Block.class));

        // when
        subject.terminate();

        // then
        assertThat(subject.isActive()).isFalse();
        assertThat(subject.mining)
                .isNotCancelled()
                .isCompletedExceptionally();

        final Throwable actual = catchThrowable(() -> subject.mining.get());
        assertThat(actual)
                .isExactlyInstanceOf(ExecutionException.class)
                .hasCauseExactlyInstanceOf(InterruptedException.class);
    }

    @Test
    public void should_terminate_when_miner_is_mining() {

        // given
        subject.mining = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (final InterruptedException ignored) {}

            return mock(Block.class);
        });

        // when
        subject.terminate();

        // then
        assertThat(subject.isActive()).isFalse();
        assertThat(subject.mining)
                .isNotCancelled()
                .isCompletedExceptionally();

        final Throwable actual = catchThrowable(() -> subject.mining.get());
        assertThat(actual)
                .isExactlyInstanceOf(ExecutionException.class)
                .hasCauseExactlyInstanceOf(InterruptedException.class);
    }
}
