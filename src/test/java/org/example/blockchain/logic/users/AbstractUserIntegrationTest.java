package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.builder.SimpleUserBuilder;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class AbstractUserIntegrationTest {

    private BlockChain blockchain;
    private AbstractUser subject;

    @BeforeEach
    public void setUp() throws Exception {
        final Field field = BlockChain.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);

        blockchain = spy(BlockChain.getInstance());

        subject = SimpleUserBuilder.builder()
                .withName("TestUser-1")
                .withKeyPair(mock(KeyPair.class))
                .withBlockChain(blockchain)
                .withSimulation(mock(Simulation.class))
                .build();
    }

    //########################################################//
    //                                                        //
    //                   Test 'sleep' method                  //
    //                                                        //
    //########################################################//

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void should_not_sleep_when_timeout_is_less_or_equal_to_zero(final long timeout) {

        // when
        subject.sleep(timeout, TimeUnit.MILLISECONDS);

        // then
        assertThat(subject.sleeping).isNull();
    }

    @Test
    public void should_not_sleep_when_time_unit_is_null() {

        // when
        subject.sleep(1L, null);

        // then
        assertThat(subject.sleeping).isNull();
    }

    @Test
    public void should_sleep_for_the_first_time() {

        // when
        subject.sleep(1L, TimeUnit.MILLISECONDS);

        // then
        assertThat(subject.sleeping).isCompletedWithValue(true);
    }

    @Test
    public void should_sleep_for_the_second_time() {

        // given
        final CompletableFuture<Boolean> first = CompletableFuture.completedFuture(false);
        subject.sleeping = first;

        // when
        subject.sleep(1L, TimeUnit.MILLISECONDS);

        // then
        assertThat(subject.sleeping).isNotEqualTo(first).isCompletedWithValue(true);
    }

    @Test
    public void should_cancel_sleeping_when_user_is_asleep() {

        // given
        final ExecutorService cancellationService = Executors.newSingleThreadExecutor();
        cancellationService.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(2500L);
            } catch (final InterruptedException ignored) {}

            subject.sleeping.cancel(true);
        });

        // when
        subject.sleep(10L);

        // then
        assertThat(subject.sleeping).isCancelled();
    }

    @Test
    public void should_cancel_sleeping_before_any_sleep_was_executed() {

        // given
        final CompletableFuture<Boolean> cancelled = CompletableFuture.failedFuture(new CancellationException());
        subject.sleeping = cancelled;

        // when
        subject.sleep(1, TimeUnit.DAYS);

        // then
        assertThat(subject.sleeping).isCancelled().isEqualTo(cancelled);
    }

    //########################################################//
    //                                                        //
    //                 Test 'terminate' method                //
    //                                                        //
    //########################################################//

    @Test
    public void should_terminate_when_sleeping_was_not_initialized() {

        // when
        subject.terminate();

        // then
        assertThat(subject.isActive()).isFalse();
        assertThat(subject.sleeping).isCancelled();
    }

    @Test
    public void should_terminate_when_sleeping_was_initialized() {

        // given
        subject.sleeping = CompletableFuture.completedFuture(true);

        // when
        subject.terminate();

        // then
        assertThat(subject.isActive()).isFalse();
        assertThat(subject.sleeping).isCancelled();
    }

    @Test
    public void should_terminate_when_user_is_sleeping() {

        // given
        final ExecutorService terminationService = Executors.newSingleThreadExecutor();
        terminationService.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(2500L);
            } catch (final InterruptedException ignored) {}

            subject.terminate();
        });

        // when
        subject.sleep(10L);

        // then
        assertThat(subject.isActive()).isFalse();
        assertThat(subject.sleeping).isCancelled();
    }
}
