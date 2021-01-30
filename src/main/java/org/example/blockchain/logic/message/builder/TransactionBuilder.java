package org.example.blockchain.logic.message.builder;

import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Transaction;
import org.example.blockchain.logic.users.AbstractUser;

/**
 * A builder for the {@link Transaction} class.
 *
 * @author Dominik Szmyt
 * @see Transaction
 * @since 1.0.0
 */
public final class TransactionBuilder {

    private Message message;
    private AbstractUser from;
    private AbstractUser to;
    private long amount;

    private TransactionBuilder() { }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public TransactionBuilder withMessage(final Message message) {
        this.message = message;
        return this;
    }

    public TransactionBuilder withFrom(final AbstractUser from) {
        this.from = from;
        return this;
    }

    public TransactionBuilder withTo(final AbstractUser to) {
        this.to = to;
        return this;
    }

    public TransactionBuilder withAmount(final long amount) {
        this.amount = amount;
        return this;
    }

    public Transaction build() {
        return new Transaction(message, from, to, amount);
    }
}
