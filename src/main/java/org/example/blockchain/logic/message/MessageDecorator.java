package org.example.blockchain.logic.message;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * An implementation of the <a href="https://en.wikipedia.org/wiki/Decorator_pattern" target="_blank">Decorator</a> pattern.
 * {@code MessageDecorator} is a {@link Message} that stores another {@code Message}.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public abstract class MessageDecorator implements Message {

    protected final Message message;

    /**
     * Create a {@code MessageDecorator} with a {@link Message}.
     * @param message1 A message that is to be stored by a {@code MessageDecorator}.
     */
    public MessageDecorator(final Message message1) {
        message = message1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDecorator)) return false;
        MessageDecorator that = (MessageDecorator) o;
        return message.getId() != that.message.getId() &&
                Objects.equal(message.getText(), that.message.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message.getId());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .toString();
    }

    @Override
    public String getText() {
        return message.getText();
    }

    @Override
    public int getId() {
        return message.getId();
    }
}
