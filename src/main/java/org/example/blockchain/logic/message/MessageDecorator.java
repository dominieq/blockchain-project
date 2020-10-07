package org.example.blockchain.logic.message;

public abstract class MessageDecorator implements Message {

    protected Message message;

    public MessageDecorator(Message message) {
        this.message = message;
    }

    abstract protected Message getMessage();

    abstract protected void setMessage(Message message);
}
