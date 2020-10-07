package org.example.blockchain.logic.message.builder;

import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Transaction;
import org.example.blockchain.logic.users.User;

import static java.util.Objects.isNull;

public final class TransactionBuilder {

    private Message message;
    private User from;
    private User to;
    private long amount;

    private TransactionBuilder() { }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public TransactionBuilder withMessage(final Message message) {
        this.message = message;
        return this;
    }

    public TransactionBuilder withFrom(final User from) {
        this.from = from;
        return this;
    }

    public TransactionBuilder withTo(final User to) {
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
