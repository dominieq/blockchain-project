package org.example.blockchain.logic;

import java.util.Random;

public class IdentifierStream {

    private int startingPoint;

    public IdentifierStream() {
        startingPoint = 0;
    }

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
