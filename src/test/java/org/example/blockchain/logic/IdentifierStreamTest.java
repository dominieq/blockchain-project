package org.example.blockchain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentifierStreamTest {

    private IdentifierStream subject;

    @BeforeEach
    public void setUp() {
        subject = new IdentifierStream();
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
