package org.example.blockchain.simulation;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.SecureMessage;
import org.example.blockchain.logic.message.Transaction;
import org.example.blockchain.logic.users.AbstractUser;
import org.example.blockchain.logic.users.builder.SimpleUserBuilder;
import org.example.blockchain.simulation.builder.SimulationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SimulationTest {

    private Simulation subject;

    @BeforeEach
    public void setUp() {
        subject = SimulationBuilder.builder()
                .withUsers(spy(new ArrayList<>()))
                .withUserService(mock(ExecutorService.class))
                .build();
    }

    @Test
    public void should_not_create_and_perform_transaction_for_null_user() {

        // when
        subject.createAndPerformTransaction(null);

        // then
        verifyNoInteractions(subject.getUsers());
    }

    @Test
    public void should_not_create_and_perform_transaction_when_only_one_user_in_simulation() {

        // given
        final AbstractUser user = mock(AbstractUser.class);
        subject.getUsers().add(user);

        // then
        subject.createAndPerformTransaction(user);

        // then
        assertThat(subject.getUsers()).containsOnly(user);
        verify(subject.getUsers(), times(1)).toArray();
        verifyNoInteractions(user);
    }

    @Test
    public void should_not_create_and_perform_transaction_when_user_has_zero_coins() {

        // given
        final AbstractUser chosenUser = mock(AbstractUser.class);

        final AbstractUser user = spy(SimpleUserBuilder.builder()
                .withName("dominieq")
                .withKeyPair(mock(KeyPair.class))
                .withBlockChain(mock(BlockChain.class))
                .withSimulation(subject)
                .build());

        user.takeCoins(100);
        assertThat(user.getCoins()).isZero();

        subject.getUsers().addAll(Arrays.asList(chosenUser, user));

        // when
        subject.createAndPerformTransaction(user);

        // then
        assertThat(subject.getUsers()).containsExactly(chosenUser, user);
        verify(subject.getUsers(), times(1)).toArray();
        verifyNoInteractions(chosenUser);
        verify(user, times(2)).getCoins();
    }

    @Test
    public void should_create_and_perform_transaction() {

        // given
        final BlockChain blockChain = mock(BlockChain.class);
        doReturn(true).when(blockChain).addMessage(any(Message.class));

        final AbstractUser chosenUser = spy(SimpleUserBuilder.builder()
                .withName("vulwsztyn")
                .withKeyPair(mock(KeyPair.class))
                .withBlockChain(blockChain)
                .withSimulation(subject)
                .build());

        final AbstractUser user = spy(SimpleUserBuilder.builder()
                .withName("dominieq")
                .withKeyPair(mock(KeyPair.class))
                .withBlockChain(blockChain)
                .withSimulation(subject)
                .build());
        doReturn(mock(SecureMessage.class)).when(user).prepareMessage();

        subject.getUsers().addAll(Arrays.asList(chosenUser, user));

        // when
        subject.createAndPerformTransaction(user);

        // then
        assertThat(subject.getUsers()).containsExactly(chosenUser, user);
        verify(subject.getUsers(), times(1)).toArray();
        verify(chosenUser, times(1)).addCoins(anyInt());
        verify(user, times(2)).getCoins();
        verify(user, times(1)).prepareMessage();
        verify(user, times(1)).takeCoins(anyInt());
        verify(blockChain, times(1)).addMessage(any(Transaction.class));
        verifyNoMoreInteractions(chosenUser, blockChain);
    }
}
