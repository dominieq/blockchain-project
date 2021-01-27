package org.example.blockchain.logic.message;

/**
 * An implementation of the <a href="https://en.wikipedia.org/wiki/Decorator_pattern" target="_blank">Decorator</a> pattern.
 * <tt>MessageDecorator</tt> is a {@link Message} that stores another <tt>Message</tt>.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public abstract class MessageDecorator implements Message {

    protected final Message message;

    public MessageDecorator(final Message message1) {
        message = message1;
    }

    abstract protected Message getMessage();
}
