package org.example.blockchain.logic;

import com.google.common.collect.Range;

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
    private Range<Integer> range;

    /**
     * Create an {@code IdentifierStream} which starts at the passed value.
     * @param startingPoint The starting point of an {@code IdentifierStream}.
     * @since 1.1.0
     */
    private IdentifierStream(final int startingPoint) {
        this.startingPoint = startingPoint;
        range = Range.closedOpen(startingPoint, startingPoint + 100);
    }

    /**
     * Create an {@code IdentifierStream} which starts at the passed value.
     * @param startingPoint The starting point of an {@code IdentifierStream}.
     * @return New {@code IdentifierStream} with a specified starting point.
     * @since 1.1.0
     */
    public static IdentifierStream startAt(final int startingPoint) {
        return new IdentifierStream(startingPoint);
    }

    /**
     * Generates a random stream of ints from a specified range
     * and then finds the first value that is greater than the starting point.
     * If there are no such results, increments the starting point by 1.
     * If the next value is exceeding the current range,
     * creates a new range {@code [current upper boundary; current upper boundary + 100)}.
     *
     * @return A unique identifier
     */
    public int getNext() {
        int next = new Random().ints(range.lowerEndpoint(), range.upperEndpoint())
                .filter(x -> x > startingPoint)
                .findFirst()
                .orElse(startingPoint + 1);

        if (next == range.upperEndpoint() - 1) {
            range = Range.closedOpen(range.upperEndpoint(), range.upperEndpoint() + 100);
        }

        startingPoint = next;
        return next;
    }
}
