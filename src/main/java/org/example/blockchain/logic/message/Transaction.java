package org.example.blockchain.logic.message;

import org.example.blockchain.logic.users.AbstractUser;

import static java.util.Objects.isNull;

/**
 * A <tt>Message</tt> that specifies a sender and recipient
 * as well as the number of coins that is to be transferred from the sender to the recipient.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class Transaction extends MessageDecorator {

    private final AbstractUser from;
    private final AbstractUser to;
    private final long amount;

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
    }

    @Override
    public int hashCode() {
        return message.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transaction)) {
            return false;
        }

        return message.getId() == ((Transaction) obj).getMessage().getId();
    }

    @Override
    public String toString() {
        return from + " says \"" + message.getText() + "\" to " + to + " and sends " + amount + " coins";
    }

    @Override
    public String getText() {
        return message.getText();
    }

    @Override
    public int getId() {
        return message.getId();
    }

    @Override
    public Message getMessage() {
        return message;
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
