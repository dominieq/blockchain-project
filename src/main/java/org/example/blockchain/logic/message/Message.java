package org.example.blockchain.logic.message;

/**
 * Represents a medium that is sent between users and stored in one of blockchain's block.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public interface Message {

    /**
     * Each {@code Message} implementation should have a text.
     * @return A message's text.
     */
    String getText();

    /**
     * Each {@code Message} implementation should have a unique identifier.
     * @return A message's unique identifier.
     */
    int getId();
}
