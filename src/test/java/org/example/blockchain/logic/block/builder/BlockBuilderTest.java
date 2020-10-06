package org.example.blockchain.logic.block.builder;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.messages.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockBuilderTest {

    private final String dummyHash = "testHash";
    private final String dummyPreviousHash = "previousTestHash";
    private final List<Message> dummyMessages = new ArrayList<>();

    private BlockBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = BlockBuilder.builder();
    }

    @Test
    public void should_build_valid_block() {

        // when
        Block actual = subject
                .withId(1L)
                .withTimestamp(2L)
                .withMagicNumber(3)
                .withHash(dummyHash)
                .withPreviousHash(dummyPreviousHash)
                .withCreatedBy(4L)
                .withGenerationTime(5L)
                .withNProgress(6)
                .withMessages(dummyMessages)
                .build();

        // then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("timestamp", 2L)
                .hasFieldOrPropertyWithValue("magicNumber", 3)
                .hasFieldOrPropertyWithValue("hash", dummyHash)
                .hasFieldOrPropertyWithValue("previousHash", dummyPreviousHash)
                .hasFieldOrPropertyWithValue("createdBy", 4L)
                .hasFieldOrPropertyWithValue("generationTime", 5L)
                .hasFieldOrPropertyWithValue("nProgress", 6)
                .hasFieldOrPropertyWithValue("messages", dummyMessages);
    }

    @Test
    public void should_build_valid_block_from_other() {

        // given
        Block initialBlock = subject
                .withId(1L)
                .withTimestamp(2L)
                .withMagicNumber(3)
                .withHash(dummyHash)
                .withPreviousHash(dummyPreviousHash)
                .withCreatedBy(4L)
                .withGenerationTime(5L)
                .withNProgress(6)
                .withMessages(dummyMessages)
                .build();

        // when
        Block actual = subject
                .from(initialBlock)
                .build();

        // then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("timestamp", 2L)
                .hasFieldOrPropertyWithValue("magicNumber", 3)
                .hasFieldOrPropertyWithValue("hash", dummyHash)
                .hasFieldOrPropertyWithValue("previousHash", dummyPreviousHash)
                .hasFieldOrPropertyWithValue("createdBy", 4L)
                .hasFieldOrPropertyWithValue("generationTime", 5L)
                .hasFieldOrPropertyWithValue("nProgress", 6)
                .hasFieldOrPropertyWithValue("messages", dummyMessages);
    }

    @Test
    public void should_build_valid_block_from_other_with_new_values() {

        // given
        Block initialBlock = subject
                .withId(1L)
                .withTimestamp(2L)
                .withMagicNumber(3)
                .withHash(dummyHash)
                .withPreviousHash(dummyPreviousHash)
                .withCreatedBy(4L)
                .withGenerationTime(5L)
                .withNProgress(6)
                .withMessages(dummyMessages)
                .build();

        // when
        final String newHash = "newTestHash";
        final String newPreviousHash = "newPreviousTestHash";
        final List<Message> newMessages = new ArrayList<>();

        Block actual = subject.from(initialBlock)
                .withId(2L)
                .withTimestamp(3L)
                .withMagicNumber(4)
                .withHash(newHash)
                .withPreviousHash(newPreviousHash)
                .withCreatedBy(5L)
                .withGenerationTime(6L)
                .withNProgress(7)
                .withMessages(newMessages)
                .build();

        // then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("timestamp", 3L)
                .hasFieldOrPropertyWithValue("magicNumber", 4)
                .hasFieldOrPropertyWithValue("hash", newHash)
                .hasFieldOrPropertyWithValue("previousHash", newPreviousHash)
                .hasFieldOrPropertyWithValue("createdBy", 5L)
                .hasFieldOrPropertyWithValue("generationTime", 6L)
                .hasFieldOrPropertyWithValue("nProgress", 7)
                .hasFieldOrPropertyWithValue("messages", newMessages);
    }
}
