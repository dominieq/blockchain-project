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

        // when
        final boolean actual = subject.validateBlock(getValidBlockForTests().build());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    public void should_return_false_when_validating_invalid_block() {

        // given
        final Block block = getValidBlockForTests().withHash("0").build();

        // when
        final boolean actual = subject.validateBlock(block);

        // then
        assertThat(actual).isFalse();
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
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "333" + 1L + -1);
        doReturn(prevHash).when(prevBlock).getHash();
        doReturn("666").when(prevBlock).getPreviousHash();

        final Block block = getMockedBlock();
        doReturn(Blocks.applySha256(1L + 1L + prevHash + 1L + -1)).when(block).getHash();
        doReturn(prevHash).when(block).getPreviousHash();

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
        final Block block = getMockedBlock();
        doReturn("0").when(block).getHash();
        doReturn("0").when(block).getPreviousHash();

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
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn(prevHash).when(prevBlock).getHash();
        doReturn("0").when(prevBlock).getPreviousHash();

        final Block block = getMockedBlock();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + -1)).when(block).getHash();
        doReturn("0").when(block).getPreviousHash();

        // when
        final boolean actual = subject.validateBlockPair(prevBlock, block);

        // then
        assertThat(actual).isFalse();
        verifyBlockPairWasValidated(prevBlock, block, 1);
    }

    @Test
    public void should_return_true_when_validating_ordered_pair_of_valid_blocks() {

        // given
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn(prevHash).when(prevBlock).getHash();
        doReturn("0").when(prevBlock).getPreviousHash();

        final Block block = getMockedBlock();
        doReturn(Blocks.applySha256(1L + 1L + prevHash + 1L + -1)).when(block).getHash();
        doReturn(prevHash).when(block).getPreviousHash();

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
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + -1)).when(block).getHash();
        doReturn(Collections.emptyList()).when(block).getMessages();

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
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn("0").when(block).getHash();
        doReturn(Collections.emptyList()).when(block).getMessages();

        // when
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
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn("0").when(prevBlock).getPreviousHash();
        doReturn(prevHash).when(prevBlock).getHash();
        doReturn(Collections.emptyList()).when(prevBlock).getMessages();

        final Block block = getMockedBlock();
        doReturn(prevHash).when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + prevHash + 1L + -1)).when(block).getHash();
        doReturn(Collections.emptyList()).when(block).getMessages();

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
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn("0").when(prevBlock).getPreviousHash();
        doReturn(prevHash).when(prevBlock).getHash();
        doReturn(Collections.emptyList()).when(prevBlock).getMessages();

        final Block block = getMockedBlock();
        doReturn(prevHash).when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "666" + 1L + -1)).when(block).getHash();
        doReturn(Collections.emptyList()).when(block).getMessages();

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
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn("0").when(prevBlock).getPreviousHash();
        doReturn(prevHash).when(prevBlock).getHash();
        doReturn(Collections.emptyList()).when(prevBlock).getMessages();

        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + -1)).when(block).getHash();
        doReturn(Collections.emptyList()).when(block).getMessages();

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
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn("0").when(prevBlock).getPreviousHash();
        doReturn(prevHash).when(prevBlock).getHash();

        final Message prevMessage = mock(Message.class);
        doReturn(1).when(prevMessage).getId();
        doReturn(Collections.singletonList(prevMessage)).when(prevBlock).getMessages();

        final Block block = getMockedBlock();
        doReturn(prevHash).when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + prevHash + 1L + -1)).when(block).getHash();

        final Message message = mock(Message.class);
        doReturn(2).when(message).getId();
        doReturn(Collections.singletonList(message)).when(block).getMessages();

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
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn("0").when(prevBlock).getPreviousHash();
        doReturn(prevHash).when(prevBlock).getHash();

        final Message prevMessage = mock(Message.class);
        doReturn(prevId).when(prevMessage).getId();
        doReturn(Collections.singletonList(prevMessage)).when(prevBlock).getMessages();

        final Block block = getMockedBlock();
        doReturn(prevHash).when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + prevHash + 1L + -1)).when(block).getHash();

        final Message message = mock(Message.class);
        doReturn(1).when(message).getId();
        doReturn(Collections.singletonList(message)).when(block).getMessages();

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
        assertThat(subject.getBlocks()).isEmpty();
    }

    @Test
    public void should_not_put_invalid_block() {

        // given
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn("0").when(block).getHash();

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.getBlocks()).isEmpty();
        verifyBlockWasValidated(block, 1);
    }

    @Test
    public void should_not_put_block_because_not_enough_zeros()
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + -1)).when(block).getHash();

        Field field = BlockChain.class.getDeclaredField("numberOfZeros");
        field.setAccessible(true);
        field.set(subject, 2);

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.getBlocks()).isEmpty();
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, never()).getMessages();
    }

    @Test
    public void should_not_put_block_because_last_pair_is_unordered() {

        // given
        final Block prevBlock = getMockedBlock();
        doReturn("333").when(prevBlock).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "333" + 1L + -1)).when(prevBlock).getHash();

        final Block block = getMockedBlock();
        doReturn("666").when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "666" + 1L + -1)).when(block).getHash();

        subject.getBlocks().add(prevBlock);

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isFalse();
        assertThat(subject.getBlocks()).containsOnly(prevBlock);
        verifyBlockPairWasValidated(prevBlock, block, 1);
        verify(block, never()).getMessages();

    }

    @Test
    public void should_put_valid_block_when_block_list_is_empty() {

        // given
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + -1)).when(block).getHash();

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.getBlocks()).contains(block);
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, times(1)).getMessages();
    }

    @Test
    public void should_put_valid_block_when_block_list_is_not_empty() {

        // given
        final Block prevBlock = getMockedBlock();
        final String prevHash = Blocks.applySha256(1L + 1L + "0" + 1L + -1);
        doReturn("0").when(prevBlock).getPreviousHash();
        doReturn(prevHash).when(prevBlock).getHash();

        final Block block = getMockedBlock();
        doReturn(prevHash).when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + prevHash + 1L + -1)).when(block).getHash();

        subject.getBlocks().add(prevBlock);

        // when
        final boolean actual = subject.putLast(block);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.getBlocks()).containsExactly(prevBlock, block);
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
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + -1)).when(block).getHash();

        // when
        final boolean actual = subject.putLast(block, 59);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.getBlocks()).contains(block);
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, times(1)).getMessages();
        verify(block, times(1)).setNProgress(1);
        assertThat(subject.getNumberOfZeros()).isOne();
    }

    @Test
    public void should_not_increase_number_of_zeros()
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn(-1741077192).when(block).getMagicNumber();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + -1741077192)).when(block).getHash();

        final Field field = BlockChain.class.getDeclaredField("numberOfZeros");
        field.setAccessible(true);
        field.set(subject, 3);

        // when
        final boolean actual = subject.putLast(block, 59);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.getBlocks()).contains(block);
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, times(1)).getMessages();
        verify(block, times(1)).setNProgress(3);
        assertThat(subject.getNumberOfZeros()).isEqualTo(3);
    }

    @Test
    public void should_decrease_number_of_zeros()
            throws NoSuchFieldException, IllegalAccessException {

        // given
        final Block block = getMockedBlock();
        doReturn("0").when(block).getPreviousHash();
        doReturn(2114227617).when(block).getMagicNumber();
        doReturn(Blocks.applySha256(1L + 1L + "0" + 1L + 2114227617)).when(block).getHash();

        final Field field = BlockChain.class.getDeclaredField("numberOfZeros");
        field.setAccessible(true);
        field.set(subject, 1);

        // when
        final boolean actual = subject.putLast(block, 60);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.getBlocks()).contains(block);
        verifyAttemptToAddBlockToEmptyList(block, 1);
        verify(block, times(1)).getMessages();
        verify(block, times(1)).setNProgress(0);
        assertThat(subject.getNumberOfZeros()).isZero();
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
        assertThat(subject.getMessages()).isEmpty();
    }

    @Test
    public void should_add_message_when_list_is_empty() {

        // given
        final Message message = mock(Message.class);

        // when
        final Boolean actual = subject.addMessage(message);

        // then
        assertThat(actual).isTrue();
        assertThat(subject.getMessages()).containsOnly(message);
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
        assertThat(subject.getMessages()).containsOnly(prevMessage);
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
        assertThat(subject.getMessages()).containsExactly(prevMessage, message);
    }

    private Block getMockedBlock() {
        final Block block = mock(Block.class);
        doReturn(1L).when(block).getId();
        doReturn(1L).when(block).getTimestamp();
        doReturn(1L).when(block).getCreatedBy();
        doReturn(-1).when(block).getMagicNumber();
        return block;
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

    private BlockBuilder getValidBlockForTests() {
        return BlockBuilder.builder()
                .withId(1L)
                .withTimestamp(1L)
                .withMagicNumber(-1)
                .withHash(Blocks.applySha256(1L + 1L + "0" + 1L  + -1))
                .withPreviousHash("0")
                .withCreatedBy(1L)
                .withGenerationTime(5L)
                .withNProgress(0)
                .withMessages(new ArrayList<>());
    }
}
