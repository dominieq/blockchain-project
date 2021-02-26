package org.example.blockchain.logic.message.builder;

import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Transaction;
import org.example.blockchain.logic.users.AbstractUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

public class TransactionBuilderTest {

    private TransactionBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = TransactionBuilder.builder();
    }

    @Test
    public void should_build_valid_transaction() {

        // given
        final Message message = mock(Message.class);
        final AbstractUser sender = mock(AbstractUser.class);
        final AbstractUser receiver = mock(AbstractUser.class);

        // when
        final Transaction actual = subject
                .withMessage(message)
                .withFrom(sender)
                .withTo(receiver)
                .withAmount(100L)
                .build();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getFrom()).isEqualTo(sender);
        assertThat(actual.getTo()).isEqualTo(receiver);
        assertThat(actual.getAmount()).isEqualTo(100L);
        assertThat(actual).hasFieldOrPropertyWithValue("message", message);
    }

    @Test
    public void should_not_build_transaction_when_from_or_to_are_null() {

        // given
        final Message message = mock(Message.class);

        // when
        final Throwable actual = catchThrowable(() -> subject
                .withMessage(message)
                .withAmount(100L)
                .build());

        // then
        assertThat(actual)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction should have a sender and receiver")
                .hasNoCause();
    }
}
