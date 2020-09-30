package org.example.blockchain.logic;

import java.util.Random;

public class IdentifierStream {

    private int startingPoint;

    public IdentifierStream() {
        this.startingPoint = 0;
    }

    public int getNext() {
        Random random = new Random();

        int next = random.ints()
                .limit(100)
                .filter(x -> x > this.startingPoint)
                .findAny()
                .orElse(this.startingPoint + 1);

        this.startingPoint = next;
        return next;
    }
}
