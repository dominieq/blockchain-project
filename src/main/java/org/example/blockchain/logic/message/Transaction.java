package org.example.blockchain.logic.message;

import org.example.blockchain.logic.users.User;

public class Transaction extends MessageDecorator {

    private final User from;
    private final User to;
    private final long amount;

    public Transaction(final Message message,
                       final User from,
                       final User to,
                       final long amount) {

        super(message);

        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        return this.message.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transaction)) {
            return false;
        }

        return this.message.getId() == ((Transaction) obj).getMessage().getId();
    }

    @Override
    public String toString() {
        return this.from + " " + this.message.getText() + " " + this.to;
    }

    @Override
    public String getText() {
        return this.message.getText();
    }

    @Override
    public int getId() {
        return this.message.getId();
    }

    @Override
    public Message getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(Message message) {
        this.message = message;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public long getAmount() {
        return amount;
    }
}
