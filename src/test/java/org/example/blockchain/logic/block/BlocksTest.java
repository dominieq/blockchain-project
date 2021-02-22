package org.example.blockchain.logic.block;

import org.example.blockchain.logic.message.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BlocksTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/apply-sha256-test.csv", numLinesToSkip = 1)
    public void should_apply_sha256(String input, String expected) {

        // when
        String actual = Blocks.applySha256(input);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/find-magic-number-test.csv", numLinesToSkip = 1)
    public void should_find_magic_number(int numberOfZeros, String input) {

        // when
        int magicNumber = Blocks.findMagicNumber(numberOfZeros, input);
        String actual = Blocks.applySha256(input + magicNumber);

        // then
        assertThat(actual).startsWith("0".repeat(numberOfZeros));
    }

    @Test
    public void should_mine_first_block_when_previous_block_was_null() {

        // given
        final long timestamp = new Date().getTime();
        final long createdBy = Thread.currentThread().getId();

        // when
        Block actual;
        try (MockedStatic<Blocks> mockedBlocks = mockStatic(Blocks.class)) {
            final String input = 1L + timestamp + "0" + createdBy;

            mockedBlocks.when(() -> Blocks.findMagicNumber(0, input)).thenReturn(1);
            mockedBlocks.when(() -> Blocks.applySha256(input + 1)).thenReturn("1");
            mockedBlocks.when(() -> Blocks.mineBlock(null, new ArrayList<>(), 0, timestamp, createdBy))
                    .thenCallRealMethod();

            actual = Blocks.mineBlock(null, new ArrayList<>(), 0, timestamp, createdBy);

            mockedBlocks.verify(times(1), () -> Blocks.findMagicNumber(0, input));
            mockedBlocks.verify(times(1), () -> Blocks.applySha256(input + 1));
        }

        // then
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTimestamp()).isEqualTo(timestamp);
        assertThat(actual.getMagicNumber()).isEqualTo(1);
        assertThat(actual.getHash()).isEqualTo("1");
        assertThat(actual.getPreviousHash()).isEqualTo("0");
        assertThat(actual.getCreatedBy()).isEqualTo(createdBy);
        assertThat(actual.getNProgress()).isEqualTo(0);
        assertThat(actual.getMessages()).isEmpty();
    }

    @Test
    public void should_not_include_messages_when_first_block() {

        // given
        final List<Message> messages = new ArrayList<>(Collections.singletonList(mock(Message.class)));

        // when
        final Block actual = Blocks.mineBlock(null, messages, 0, 1L, 1L);

        // then
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTimestamp()).isEqualTo(1L);
        assertThat(actual.getPreviousHash()).isEqualTo("0");
        assertThat(actual.getCreatedBy()).isEqualTo(1L);
        assertThat(actual.getNProgress()).isEqualTo(0);
        assertThat(actual.getMessages()).isEmpty();
    }

    @Test
    public void should_mine_next_block_when_previous_block_exists() {

        // given
        final long prevTimestamp = new Date().getTime();
        final long createdBy = Thread.currentThread().getId();
        final Block prevBlock = Blocks.mineBlock(null, new ArrayList<>(),0, prevTimestamp, createdBy);

        final long timestamp = new Date().getTime();

        // when
        Block actual;
        try (MockedStatic<Blocks> mockedBlocks = mockStatic(Blocks.class)) {
            final String input = 2L + timestamp + prevBlock.getHash() + createdBy;

            mockedBlocks.when(() -> Blocks.findMagicNumber(1, input)).thenReturn(1);
            mockedBlocks.when(() -> Blocks.applySha256(input + 1)).thenReturn("1");
            mockedBlocks.when(() -> Blocks.mineBlock(prevBlock, new ArrayList<>(),1, timestamp, createdBy))
                    .thenCallRealMethod();

            actual = Blocks.mineBlock(prevBlock, new ArrayList<>(), 1, timestamp, createdBy);

            mockedBlocks.verify(times(1), () -> Blocks.findMagicNumber(1, input));
            mockedBlocks.verify(times(1), () -> Blocks.applySha256(input + 1));
        }

        // then
        assertThat(actual.getId()).isEqualTo(2L);
        assertThat(actual.getTimestamp()).isEqualTo(timestamp);
        assertThat(actual.getMagicNumber()).isEqualTo(1);
        assertThat(actual.getHash()).isEqualTo("1");
        assertThat(actual.getPreviousHash()).isEqualTo(prevBlock.getHash());
        assertThat(actual.getCreatedBy()).isEqualTo(createdBy);
        assertThat(actual.getNProgress()).isEqualTo(1);
        assertThat(actual.getMessages()).isEmpty();
    }

    @Test
    public void should_include_messages_when_not_first_block() {

        // given
        final Block prevBlock = Blocks.mineBlock(null, new ArrayList<>(), 0, 1L, 1L);
        final List<Message> messages = new ArrayList<>(Collections.singletonList(mock(Message.class)));

        // when
        final Block actual = Blocks.mineBlock(prevBlock, messages, 1, 2L, 2L);

        // then
        assertThat(actual.getId()).isEqualTo(2L);
        assertThat(actual.getTimestamp()).isEqualTo(2L);
        assertThat(actual.getPreviousHash()).isEqualTo(prevBlock.getHash());
        assertThat(actual.getCreatedBy()).isEqualTo(2L);
        assertThat(actual.getNProgress()).isEqualTo(1);
        assertThat(actual.getMessages()).isEqualTo(messages);
    }
}
