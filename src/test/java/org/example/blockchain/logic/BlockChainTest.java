package org.example.blockchain.logic;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.block.Blocks;
import org.example.blockchain.logic.block.builder.BlockBuilder;
import org.example.blockchain.logic.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BlockChainTest {

    private BlockChain subject;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        final Field field = BlockChain.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);

        subject = BlockChain.getInstance();
    }

    //########################################################//
    //                                                        //
    //               Test 'validateBlock' method              //
    //                                                        //
    //########################################################//

    @Test
    public void should_return_false_when_validating_null_block() {

        // when
        final boolean actual = subject.validateBlock(null);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    public void should_return_true_when_validating_block() {

        // given
        final Block block = spy(getBlock().build());

        // when
        final boolean actual = subject.validateBlock(block);

        // then
        assertThat(actual).isTrue();
        verifyBlockWasValidated(block, 1);
    }

    @Test
    public void should_return_false_when_validating_invalid_block() {

        // given
        final Block block = spy(getBlock().withHash("0").build());

        // when
        final boolean actual = subject.validateBlock(block);

        // then
        assertThat(actual).isFalse();
        verifyBlockWasValidated(block, 1);
    }

    //########################################################//
    //                                                        //
    //            Test 'validateBlockPair' method             //
    //                                                        //
    //########################################################//

    @Test
    public void should_return_false_when_validating_null_block_pair() {

        // when
        final boolean actual = subject.validateBlockPair(null, null);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    public void should_return_true_when_validating_ordered_pair_with_invalid_prev_block() {

        // given
        final String prevHash = Blocks.applySha256(1L + 1L + "333" + 1L + -1);
        final Block prevBlock = spy(getBlock()
                .withPreviousHash("666")
                .withHash(prevHash)
                .build());

        final Block block = spy(getBlock()
                .withPreviousHash(prevHash)
                .withHash(Blocks.applySha256(1L + 1L + prevHash + 1L + -1))
                .build());

        // when
        final boolean actual = subject.validateBlockPair(prevBlock, block);

        // then
        assertThat(actual).isFalse();
        verifyBlockPairWasValidated(prevBlock, block, 1);
    }

    @Test
    public void should_return_false_when_validating_block_pair_with_invalid_block() {

        // given
        final Block prevBlock = mock(Block.class);
        final Block block = spy(getBlock().withHash("0").build());

        // when
        final boolean actual = subject.validateBlockPair(prevBlock, block);

        // then
        assertThat(actual).isFalse();
        verifyNoInteractions(prevBlock);
        verifyBlockWasValidated(block, 1);
    }

    @Test
    public void should_return_false_when_validating_unordered_pair_of_valid_block() {

        // given
        final Block prevBlock = spy(getBlock().build());
        final Block block = spy(getBlock().build());

        // when
        final boolean actual = subject.validateBlockPair(prevBlock, block);

        // then
        assertThat(actual).isFalse();
        verifyBlockPairWasValidated(prevBlock, block, 1);
    }

    @Test
    public void should_return_true_when_validating_ordered_pair_of_valid_blocks() {

        // given
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        final Block prevBlock = spy(getBlock().build());
        final Block block = spy(getBlock()
                .withPreviousHash(prevHash)
                .withHash(Blocks.applySha256(1L + 1L + prevHash + 1L + -1))
                .build());

        // when
        final boolean actual = subject.validateBlockPair(prevBlock, block);

        // then
        assertThat(actual).isTrue();
        verifyBlockPairWasValidated(prevBlock, block, 1);
    }

    //########################################################//
    //                                                        //
    //           Test 'validateMessagePair' method            //
    //                                                        //
    //########################################################//

    @Test
    public void should_return_false_when_validating_null_message_pair() {

        // when
        final boolean actual = subject.validateMessagePair(null, null);

        // then
        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void should_return_false_when_validating_unordered_pair(final int prevId) {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(prevId).when(prevMessage).getId();

        final Message message = mock(Message.class);
        doReturn(1).when(message).getId();

        // when
        final boolean actual = subject.validateMessagePair(prevMessage, message);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    public void should_return_true_when_validation_ordered_pair() {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(1).when(prevMessage).getId();

        final Message message = mock(Message.class);
        doReturn(2).when(message).getId();

        // when
        final boolean actual = subject.validateMessagePair(prevMessage, message);

        // then
        assertThat(actual).isTrue();
    }

    //########################################################//
    //                                                        //
    //              Test 'validateMessages' method            //
    //                                                        //
    //########################################################//

    @Test
    public void should_return_true_when_messages_list_is_empty() {

        // when
        final boolean actual = subject.validateMessages(Collections.emptyList());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    public void should_return_true_when_messages_list_contains_only_one_message() {

        // given
        final Message message = mock(Message.class);

        // when
        final boolean actual = subject.validateMessages(Collections.singletonList(message));

        // then
        assertThat(actual).isTrue();
        verifyNoInteractions(message);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void should_return_false_when_messages_list_contains_unordered_pair(final int prevId) {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(prevId).when(prevMessage).getId();

        final Message message = mock(Message.class);
        doReturn(1).when(message).getId();

        // when
        final boolean actual = subject.validateMessages(Arrays.asList(prevMessage, message));

        // then
        assertThat(actual).isFalse();
        verify(prevMessage, times(1)).getId();
        verify(message, times(1)).getId();
    }

    @Test
    public void should_return_true_when_messages_list_contains_only_ordered_pairs() {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(1).when(prevMessage).getId();

        final Message message = mock(Message.class);
        doReturn(2).when(message).getId();

        // when
        final boolean actual = subject.validateMessages(Arrays.asList(prevMessage, message));

        // then
        assertThat(actual).isTrue();
        verify(prevMessage, times(1)).getId();
        verify(message, times(1)).getId();
    }

    //########################################################//
    //                                                        //
    //               Test 'validateBlocks' method             //
    //                                                        //
    //########################################################//

    @Test
    public void should_return_true_when_validating_empty_blocks_list() {

        // when
        final boolean actual = subject.validateBlocks(Collections.emptyList());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    public void should_return_true_when_validating_blocks_with_only_one_valid_block() {

        // given
        final Block block = spy(getBlock().build());

        // when
        final boolean actual = subject.validateBlocks(Collections.singletonList(block));

        // then
        assertThat(actual).isTrue();
        verifyBlockWasValidated(block, 1);
        verify(block, never()).getMessages();
    }

    @Test
    public void should_return_false_when_validating_blocks_with_only_one_invalid_block() {

        // given
        final Block block = spy(getBlock().withHash("0").build());

        // when
        final boolean actual = subject.validateBlocks(Collections.singletonList(block));

        // then
        assertThat(actual).isFalse();
        verifyBlockWasValidated(block, 1);
        verify(block, never()).getMessages();
    }

    @Test
    public void should_return_true_when_validating_blocks_with_only_ordered_pairs_of_valid_blocks() {

        // given
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        final Block prevBlock = spy(getBlock().build());
        final Block block = spy(getBlock()
                .withPreviousHash(prevHash)
                .withHash(Blocks.applySha256(1L + 1L + prevHash + 1L + -1))
                .build());

        // when
        final boolean actual = subject.validateBlocks(Arrays.asList(prevBlock, block));

        // then
        assertThat(actual).isTrue();
        verifyBlocksWereValidated(Arrays.asList(prevBlock, block), 1);
        verify(prevBlock, times(1)).getMessages();
        verify(block, times(1)).getMessages();
    }

    @Test
    public void should_return_false_when_validating_blocks_with_at_least_one_ordered_pair_of_invalid_blocks() {

        // given
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        final Block prevBlock = spy(getBlock().build());
        final Block block = spy(getBlock()
                .withPreviousHash(prevHash)
                .withHash(Blocks.applySha256(1L + 1L + "666" + 1L + -1))
                .build());

        // when
        final boolean actual = subject.validateBlocks(Arrays.asList(prevBlock, block));

        // then
        assertThat(actual).isFalse();
        verifyBlockWasValidated(prevBlock, 1);
        verifyBlockWasValidated(block, 1);
        verify(prevBlock, never()).getMessages();
        verify(block, never()).getMessages();
    }

    @Test
    public void should_return_false_when_validating_blocks_with_at_least_one_unordered_pair_of_valid_blocks() {

        // given
        final Block prevBlock = spy(getBlock().build());
        final Block block = spy(getBlock().build());

        // when
        final boolean actual = subject.validateBlocks(Arrays.asList(prevBlock, block));

        // then
        assertThat(actual).isFalse();
        verifyBlocksWereValidated(Arrays.asList(prevBlock, block), 1);
        verify(prevBlock, never()).getMessages();
        verify(block, never()).getMessages();
    }

    @Test
    public void should_return_true_when_validating_ordered_and_valid_block_list_with_ordered_messages() {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(1).when(prevMessage).getId();
        final Message message = mock(Message.class);
        doReturn(2).when(message).getId();

        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        final Block prevBlock = spy(getBlock()
                .withMessages(Collections.singletonList(prevMessage))
                .build());
        final Block block = spy(getBlock()
                .withPreviousHash(prevHash)
                .withHash(Blocks.applySha256(1L + 1L + prevHash + 1L + -1))
                .withMessages(Collections.singletonList(message))
                .build());

        // when
        final boolean actual = subject.validateBlocks(Arrays.asList(prevBlock, block));

        // then
        assertThat(actual).isTrue();
        verifyBlocksWereValidated(Arrays.asList(prevBlock, block), 1);
        verify(prevBlock, times(1)).getMessages();
        verify(block, times(1)).getMessages();
        verify(prevMessage, times(1)).getId();
        verify(message, times(1)).getId();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void should_return_false_when_validating_ordered_and_valid_block_list_but_with_unordered_messages(final int prevId) {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(prevId).when(prevMessage).getId();
        final Message message = mock(Message.class);
        doReturn(1).when(message).getId();

        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        final Block prevBlock = spy(getBlock()
                .withMessages(Collections.singletonList(prevMessage))
                .build());
        final Block block = spy(getBlock()
                .withPreviousHash(prevHash)
                .withHash(Blocks.applySha256(1L + 1L + prevHash + 1L + -1))
                .withMessages(Collections.singletonList(message))
                .build());

        // when
        final boolean actual = subject.validateBlocks(Arrays.asList(prevBlock, block));

        // then
        assertThat(actual).isFalse();
        verifyBlocksWereValidated(Arrays.asList(prevBlock, block), 1);
        verify(prevBlock, times(1)).getMessages();
        verify(block, times(1)).getMessages();
        verify(prevMessage, times(1)).getId();
        verify(message, times(1)).getId();
    }

    //########################################################//
    //                                                        //
    //     Test 'putLast' method without 'generationTime'     //
    //                                                        //
    //########################################################//

    @Test
    public void should_not_put_null_block() {

        // when
        final boolean actual = subject.putLast(null);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.blocks).isEmpty();
    }

    @Test
    public void should_not_put_invalid_block() {

        // given
        final Block block = spy(getBlock().withHash("0").build());

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.blocks).isEmpty();
        verifyBlockWasValidated(block, 1);
    }

    @Test
    public void should_not_put_block_because_not_enough_zeros()
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final Block block = spy(getBlock().build());

        Field field = BlockChain.class.getDeclaredField("numberOfZeros");
        field.setAccessible(true);
        field.set(subject, 2);

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.blocks).isEmpty();
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, never()).getMessages();
    }

    @Test
    public void should_not_put_block_because_last_pair_is_unordered() {

        // given
        final Block prevBlock = spy(getBlock()
                .withPreviousHash("333")
                .withHash(Blocks.applySha256(1L + 1L + "333" + 1L + -1))
                .build());
        final Block block = spy(getBlock()
                .withPreviousHash("666")
                .withHash(Blocks.applySha256(1L + 1L + "666" + 1L + -1))
                .build());

        subject.blocks.add(prevBlock);

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.blocks).containsOnly(prevBlock);
        verifyBlockPairWasValidated(prevBlock, block, 1);
        verify(block, never()).getMessages();

    }

    @Test
    public void should_put_valid_block_when_block_list_is_empty() {

        // given
        final Block block = spy(getBlock().build());

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.blocks).contains(block);
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, times(1)).getMessages();
    }

    @Test
    public void should_put_valid_block_when_block_list_is_not_empty() {

        // given
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        final Block prevBlock = spy(getBlock().build());
        final Block block = spy(getBlock()
                .withPreviousHash(prevHash)
                .withHash(Blocks.applySha256(1L + 1L + prevHash + 1L + -1))
                .build());

        subject.blocks.add(prevBlock);

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.blocks).containsExactly(prevBlock, block);
        verifyAttemptToAddBlockToList(prevBlock, block, 1);
        verify(block, times(1)).getMessages();
    }

    //########################################################//
    //                                                        //
    //       Test 'putLast' method with 'generationTime'      //
    //                                                        //
    //########################################################//

    @Test
    public void should_increase_number_of_zeros() {

        // given
        final Block block = spy(getBlock().build());

        // when
        final boolean actual = subject.putLast(block, 29);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.blocks).contains(block);
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, times(1)).getMessages();
        verify(block, times(1)).setNProgress(1);
        assertThat(subject.getNumberOfZeros()).isOne();
        assertThat(block.getNProgress()).isOne();
    }

    @Test
    public void should_decrease_number_of_zeros()
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final Block block = spy(getBlock()
                .withHash(Blocks.applySha256(1L + 1L + "0" + 1L + 2114227617))
                .withMagicNumber(2114227617)
                .build());

        final Field field = BlockChain.class.getDeclaredField("numberOfZeros");
        field.setAccessible(true);
        field.set(subject, 1);

        // when
        final boolean actual = subject.putLast(block, 30);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.blocks).contains(block);
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, times(1)).getMessages();
        verify(block, times(1)).setNProgress(0);
        assertThat(subject.getNumberOfZeros()).isZero();
        assertThat(block.getNProgress()).isZero();
    }

    //########################################################//
    //                                                        //
    //                Test 'addMessage' method                //
    //                                                        //
    //########################################################//

    @Test
    public void should_not_add_null_message() {

        // when
        final boolean actual = subject.addMessage(null);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.messages).isEmpty();
    }

    @Test
    public void should_add_message_when_list_is_empty() {

        // given
        final Message message = mock(Message.class);

        // when
        final Boolean actual = subject.addMessage(message);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.messages).containsOnly(message);
        verifyNoInteractions(message);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void should_not_add_message_when_last_pair_is_unordered(final int id)
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(2).when(prevMessage).getId();

        final Field field = BlockChain.class.getDeclaredField("messages");
        field.setAccessible(true);
        field.set(subject, new ArrayList<>(Collections.singletonList(prevMessage)));

        final Message message = mock(Message.class);
        doReturn(id).when(message).getId();

        // when
        final boolean actual = subject.addMessage(message);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.messages).containsOnly(prevMessage);
        verify(prevMessage, times(1)).getId();
        verify(message, times(1)).getId();
    }

    @Test
    public void should_add_message_when_last_pair_is_ordered()
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final Message prevMessage = mock(Message.class);
        doReturn(1).when(prevMessage).getId();

        final Field field = BlockChain.class.getDeclaredField("messages");
        field.setAccessible(true);
        field.set(subject, new ArrayList<>(Collections.singletonList(prevMessage)));

        final Message message = mock(Message.class);
        doReturn(2).when(message).getId();

        // when
        final boolean actual = subject.addMessage(message);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.messages).containsExactly(prevMessage, message);
        verify(prevMessage, times(1)).getId();
        verify(message, times(1)).getId();
    }

    //########################################################//
    //                                                        //
    //                   Test other methods                   //
    //                                                        //
    //########################################################//

    @Test
    public void should_get_the_same_instance() {

        // when
        final BlockChain expected = BlockChain.getInstance();

        // then
        assertThat(subject).isEqualTo(expected);
    }

    @Test
    public void should_get_unique_identifier()
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final IdentifierStream identifierStream = mock(IdentifierStream.class);
        doReturn(1).when(identifierStream).getNext();

        final Field field = BlockChain.class.getDeclaredField("identifierStream");
        field.setAccessible(true);
        field.set(subject, identifierStream);

        // when
        final int actual = subject.getUniqueIdentifier();

        // then
        assertThat(actual).isOne();
        verify(identifierStream, times(1)).getNext();
    }

    private void verifyBlockWasValidated(final Block block, final int times) {
        verify(block, times(times)).getId();
        verify(block, times(times)).getTimestamp();
        verify(block, times(times)).getHash();
        verify(block, times(times)).getPreviousHash();
        verify(block, times(times)).getCreatedBy();
        verify(block, times(times)).getMagicNumber();
    }

    private void verifyAttemptToAddBlockToEmptyList(final Block block, final int times) {
        verify(block, times(times)).getId();
        verify(block, times(times)).getTimestamp();
        verify(block, times(2 * times)).getHash();
        verify(block, times(times)).getPreviousHash();
        verify(block, times(times)).getCreatedBy();
        verify(block, times(times)).getMagicNumber();
    }

    private void verifyBlockPairWasValidated(final Block prevBlock,
                                             final Block block,
                                             final int times) {

        verify(prevBlock, times(times)).getId();
        verify(prevBlock, times(times)).getTimestamp();
        verify(prevBlock, times(times)).getPreviousHash();
        verify(prevBlock, times(times)).getCreatedBy();
        verify(prevBlock, times(times)).getMagicNumber();

        verify(block, times(times)).getId();
        verify(block, times(times)).getTimestamp();
        verify(block, times(times)).getHash();
        verify(block, times(2 * times)).getPreviousHash();
        verify(block, times(times)).getCreatedBy();
        verify(block, times(times)).getMagicNumber();
    }

    private void verifyAttemptToAddBlockToList(final Block prevBlock,
                                               final Block block,
                                               final int times) {

        verify(prevBlock, times(times)).getId();
        verify(prevBlock, times(times)).getTimestamp();
        verify(prevBlock, times(times)).getPreviousHash();
        verify(prevBlock, times(times)).getCreatedBy();
        verify(prevBlock, times(times)).getMagicNumber();

        verify(block, times(times)).getId();
        verify(block, times(times)).getTimestamp();
        verify(block, times(2 * times)).getHash();
        verify(block, times(2 * times)).getPreviousHash();
        verify(block, times(times)).getCreatedBy();
        verify(block, times(times)).getMagicNumber();
    }

    private void verifyBlocksWereValidated(final List<Block> blocks, final int times) {
        for (int i = 0; i < blocks.size() - 1; i++) {
            final Block block = blocks.get(i);
            verify(block, times(2 * times)).getId();
            verify(block, times(2 * times)).getTimestamp();
            verify(block, times(times)).getHash();
            verify(block, times(2 * times)).getPreviousHash();
            verify(block, times(2 * times)).getCreatedBy();
            verify(block, times(2 * times)).getMagicNumber();
        }

        final Block lastBlock = blocks.get(blocks.size() - 1);
        verify(lastBlock, times(times)).getId();
        verify(lastBlock, times(times)).getTimestamp();
        verify(lastBlock, times(times)).getHash();
        verify(lastBlock, times(2 * times)).getPreviousHash();
        verify(lastBlock, times(times)).getCreatedBy();
        verify(lastBlock, times(times)).getMagicNumber();
    }

    private BlockBuilder getBlock() {
        return BlockBuilder.builder()
                .withId(1L)
                .withTimestamp(1L)
                .withPreviousHash("0")
                .withHash(Blocks.applySha256(1L + 1L + "0" + 1L  + -1))
                .withCreatedBy(1L)
                .withMagicNumber(-1)
                .withGenerationTime(0L)
                .withNProgress(0)
                .withMessages(new ArrayList<>());
    }
}
