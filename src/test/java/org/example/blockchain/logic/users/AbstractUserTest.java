package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.SecureMessage;
import org.example.blockchain.logic.users.builder.SimpleUserBuilder;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class AbstractUserTest {

    private static KeyPairGenerator generator;
    private final BlockChain blockChain = mock(BlockChain.class);
    private final Simulation simulation = mock(Simulation.class);
    private AbstractUser subject;

    @BeforeAll
    public static void initialize()
            throws NoSuchAlgorithmException {

        generator = KeyPairGenerator.getInstance("DSA");
        generator.initialize(2048);
    }

    @BeforeEach
    public void setUp() {
        subject = SimpleUserBuilder.builder()
                .withId(1L)
                .withName("dominieq")
                .withKeyPair(generator.generateKeyPair())
                .withBlockChain(blockChain)
                .withSimulation(simulation)
                .build();
    }

    @Test
    public void should_increase_number_of_coins_when_passing_positive_value() {

        // given
        subject.coins = 0;

        // when
        subject.addCoins(50);

        // then
        assertThat(subject.getCoins()).isEqualTo(50);
    }

    @Test
    public void should_do_nothing_when_passing_negative_value_to_add() {

        // given
        subject.coins = 50;

        // when
        subject.addCoins(-50);

        // then
        assertThat(subject.getCoins()).isEqualTo(50);
    }

    @Test
    public void should_decrease_number_of_coins_when_passing_positive_value() {

        // given
        subject.coins = 50;

        // when
        subject.takeCoins(50);

        // then
        assertThat(subject.getCoins()).isZero();
    }

    @Test
    public void should_do_nothing_when_passing_negative_value_to_take() {

        // given
        subject.coins = 0;

        // when
        subject.takeCoins(-50);

        // then
        assertThat(subject.getCoins()).isZero();
    }

    @Test
    public void should_prepare_secure_message() {

        // given
        doReturn(1).when(blockChain).getUniqueIdentifier();

        // when
        final Message actual = subject.prepareMessage();

        // then
        assertThat(actual).isInstanceOf(SecureMessage.class);
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getText()).isEqualTo("Hello there!");
        assertThat(((SecureMessage) actual).getPublicKey()).isEqualTo(subject.getPublicKey());

        final boolean actualVerify = Messages.verify(
                actual.getText() + 1, ((SecureMessage) actual).getSignature(), subject.getPublicKey());
        assertThat(actualVerify).isTrue();
    }

    @Test
    public void should_not_execute_sleep_when_timeout_is_zero() {

        // given
        final long timeout = 0L;
        final TimeUnit timeUnit = TimeUnit.SECONDS;

        // when
        final boolean actual = subject.executeSleep(timeout, timeUnit);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    public void should_not_execute_sleep_when_sleep_service_is_null() {

        // given
        final long timeout = 1L;

        // when
        final boolean actual = subject.executeSleep(timeout, null);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    public void should_execute_sleep() {

        // given
        final long timeout = 1L;
        final TimeUnit timeUnit = TimeUnit.SECONDS;

        // when
        final boolean actual = subject.executeSleep(timeout, timeUnit);

        // then
        assertThat(actual).isTrue();
    }
}
