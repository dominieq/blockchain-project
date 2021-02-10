package org.example.blockchain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentifierStreamTest {

    private IdentifierStream subject;

    @BeforeEach
    public void setUp() {
        subject = IdentifierStream.startAt(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 50, 100})
    public void should_get_unique_identifier(final int startingPoint) {

        // given
        subject = IdentifierStream.startAt(startingPoint);

        // when
        final int actual = subject.getNext();

        // then
        assertThat(actual).isBetween(startingPoint, startingPoint + 100);
    }

    @Test
    public void should_get_ten_unique_identifiers_in_ascending_order() {

        // when
        List<Integer> identifiers = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            identifiers.add(subject.getNext());
        }

        // then
        Set<Integer> identifiersSet = new HashSet<>(identifiers);

        assertThat(identifiers).isSortedAccordingTo(Comparator.naturalOrder());
        assertThat(identifiers).hasSameSizeAs(identifiersSet);
    }
}
