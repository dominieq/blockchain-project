package org.example.blockchain.logic;

import java.util.Random;

/**
 * Supplies blockchain with unique identifiers for it's messages.
 * When a user wishes to add a message to a blockchain they need to ask for a unique identifier first.
 * This identifier is then used for validating messages.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class IdentifierStream {

    private int startingPoint;

    /**
     * Create an {@code IdentifierStream} with starting point at 0.
     */
    public IdentifierStream() {
        startingPoint = 0;
    }

    /**
     * Draws a random stream of ints and then finds one value from the first one hundred results
     * that is greater than the starting point. If there are not such results, increments starting point by 1.
     *
     * @return A unique identifier
     */
    public int getNext() {
        final Random random = new Random();

        final int next = random.ints()
                .limit(100)
                .filter(x -> x > startingPoint)
                .findAny()
                .orElse(startingPoint + 1);

        startingPoint = next;
        return next;
    }
}
