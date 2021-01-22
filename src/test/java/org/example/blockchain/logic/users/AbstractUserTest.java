package org.example.blockchain.logic.users;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.SecureMessage;
import org.example.blockchain.logic.users.builder.UserBuilder;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
        subject = UserBuilder.builder()
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
        assertThat(((SecureMessage) actual).getPublicKey()).isEqualTo(subject.getKeyPair().getPublic());

        final boolean actualVerify = Messages.verify(
                actual.getText() + 1, ((SecureMessage) actual).getSignature(), subject.getKeyPair().getPublic());
        assertThat(actualVerify).isTrue();
    }
}
