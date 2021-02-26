package org.example.blockchain.logic.message;

import com.google.common.base.Objects;
import org.example.blockchain.logic.users.AbstractUser;

import static java.util.Objects.isNull;

/**
 * A {@code Message} that specifies a sender and recipient
 * as well as the number of coins that is to be transferred from the sender to the recipient.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class Transaction extends MessageDecorator {

    private final AbstractUser from;
    private final AbstractUser to;
    private final long amount;
    private final String text;

    /**
     * Create a {@code Transaction} with all necessary fields.
     * @param message A message included in a {@code Transaction}.
     * @param from The sender of a {@code Transaction}.
     * @param to The recipient of a {@code Transaction}.
     * @param amount The number of coins transferred in a transaction.
     * @throws IllegalArgumentException When the from and to arguments are not defined - {@code null}.
     */
    public Transaction(final Message message,
                       final AbstractUser from,
                       final AbstractUser to,
                       final long amount) {

        super(message);

        if (isNull(from) || isNull(to)) {
            throw new IllegalArgumentException("Transaction should have a sender and receiver");
        }

        this.from = from;
        this.to = to;
        this.amount = amount;
        this.text = from + " says \"" + message.getText() + "\" to " + to + " and sends " + amount + " coins";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode());
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public String getText() {
        return text;
    }

    public AbstractUser getFrom() {
        return from;
    }

    public AbstractUser getTo() {
        return to;
    }

    public long getAmount() {
        return amount;
    }
}
