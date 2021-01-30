package org.example.blockchain.logic.message;

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

    abstract protected Message getMessage();
}
