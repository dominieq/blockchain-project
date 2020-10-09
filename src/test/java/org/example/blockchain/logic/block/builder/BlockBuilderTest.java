package org.example.blockchain.logic.block.builder;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockBuilderTest {

    private BlockBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = BlockBuilder.builder();
    }

    @Test
    public void should_build_valid_block() {

        // given
        final String dummyHash = "testHash";
        final String dummyPreviousHash = "previousTestHash";
        final List<Message> dummyMessages = new ArrayList<>();

        // when
        final Block actual = subject
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
}
