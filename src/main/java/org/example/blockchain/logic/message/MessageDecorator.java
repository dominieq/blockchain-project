package org.example.blockchain.logic.message;

public abstract class MessageDecorator implements Message {

    protected final Message message;

    public MessageDecorator(final Message message1) {
        message = message1;
    }

    abstract protected Message getMessage();
}
