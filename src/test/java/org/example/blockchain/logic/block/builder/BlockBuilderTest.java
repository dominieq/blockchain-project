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
        final String hash = "hash";
        final String previousHash = "previousHash";
        final List<Message> messages = new ArrayList<>();

        // when
        final Block actual = subject
                .withId(1L)
                .withTimestamp(2L)
                .withMagicNumber(3)
                .withHash(hash)
                .withPreviousHash(previousHash)
                .withCreatedBy(4L)
                .withGenerationTime(5L)
                .withNProgress(6)
                .withMessages(messages)
                .build();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTimestamp()).isEqualTo(2L);
        assertThat(actual.getMagicNumber()).isEqualTo(3);
        assertThat(actual.getHash()).isEqualTo(hash);
        assertThat(actual.getPreviousHash()).isEqualTo(previousHash);
        assertThat(actual.getCreatedBy()).isEqualTo(4L);
        assertThat(actual.getGenerationTime()).isEqualTo(5L);
        assertThat(actual.getNProgress()).isEqualTo(6);
        assertThat(actual.getMessages()).isEqualTo(messages);
    }
}
