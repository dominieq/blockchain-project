package org.example.blockchain.logic;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.block.Blocks;
import org.example.blockchain.logic.message.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * The <tt>BlockChain</tt> is a simple implementation of a blockchain concept.
 * Allows {@link org.example.blockchain.logic.users.Miner}s to add blocks with completed transactions.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class BlockChain implements Serializable {

    private static BlockChain instance;
    private int numberOfZeros;
    private final List<Message> messages;
    private final List<Block> blocks;
    private final IdentifierStream identifierStream;
    private static final Object IDENTIFIER_LOCK = new Object();
    private static final Object MESSAGES_LOCK = new Object();

    private BlockChain() {
        numberOfZeros = 0;
        messages = new ArrayList<>();
        blocks = new ArrayList<>();
        identifierStream = new IdentifierStream();
    }

    public static BlockChain getInstance() {
        if (instance == null) {
            instance = new BlockChain();
        }

        return instance;
    }

    /**
     * If the {@link BlockChain} is empty, puts a block after successful validation. Otherwise, puts a block
     * after validating the last pair in the {@link BlockChain}.
     * Checks whether a block's hash starts with the required number of zeros.
     * Block is rejected if it doesn't have the required number of zeros at the beginning of it's hash.
     * If a block contains any messages, they are removed from the queue.
     *
     * @param block - A block that is to be added to the {@link BlockChain}.
     * @return <tt>true</tt> if a block was successfully added to the {@link BlockChain}, otherwise <tt>false</tt>.
     */
    public synchronized boolean putLast(final Block block) {
        if (((blocks.isEmpty() && validateBlock(block)) || validateBlockPair(getLast(), block)) &&
                block.getHash().startsWith("0".repeat(Math.max(0, numberOfZeros)))) {

            synchronized (MESSAGES_LOCK) {
                messages.removeAll(block.getMessages());
                blocks.add(block);
                return true;
            }
        }

        return false;
    }

    /**
     * At first, tries to put a block using {@link #putLast(Block)}.
     * If it succeeds, evaluates the next number of zeros at the beginning of a block's hash.
     *
     * @param block - An block that is to be added to the {@link BlockChain}.
     * @param generationTime - The amount of time it took to generate a block.
     * @return <tt>true</tt> if a block was successfully added to the {@link BlockChain}, otherwise <tt>false</tt>.
     * @see #putLast(Block)
     */
    public synchronized boolean putLast(final Block block, final long generationTime) {
        final boolean isIn = putLast(block);

        if (isIn) {
            if (generationTime < 60L) {
                if (numberOfZeros < 3) {
                    block.setNProgress(++numberOfZeros);
                } else {
                    block.setNProgress(numberOfZeros);
                }
            } else if (numberOfZeros > 0) {
                block.setNProgress(--numberOfZeros);
            }
        }

        return isIn;
    }

    /**
     * Returns the last block in the {@link BlockChain} or <tt>null</tt> if it is empty.
     * @return The last block in the {@link BlockChain} or <tt>null</tt> if it is empty.
     */
    public synchronized Block getLast() {
        return !blocks.isEmpty() ? blocks.get(blocks.size() - 1) : null;
    }

    /**
     * If the message queue is empty, adds a message without validation.
     * Otherwise, checks if a message id is greater than the id of the last message in the queue
     * and adds a message if the validation was successful.
     *
     * @param message - A message that is to be added to the queue.
     * @return <tt>true</tt> if a message was successfully added to the queue, otherwise <tt>false</tt>.
     */
    public boolean addMessage(final Message message) {
        if (isNull(message)) return false;

        synchronized (MESSAGES_LOCK) {
            if (messages.isEmpty()) {
                return messages.add(message);
            } else if (validateMessagePair(messages.get(messages.size() - 1), message)) {
                return messages.add(message);
            }
        }
        return false;
    }

    /**
     * Returns a unique identifier for messages.
     * @return A unique identifier.
     * @see IdentifierStream
     */
    public int getUniqueIdentifier() {
        synchronized (IDENTIFIER_LOCK) {
            return identifierStream.getNext();
        }
    }

    /**
     * Checks whether a block's hash was generated properly.
     *
     * @param block - A block that is to be validated.
     * @return <tt>true</tt> if a block's hash was generated properly, otherwise <tt>false</tt>.
     * @see Block
     */
    public boolean validateBlock(final Block block) {
        if (isNull(block)) return false;

        final String hash = Blocks.applySha256(
                block.getId() +
                        block.getTimestamp() +
                        block.getPreviousHash() +
                        block.getCreatedBy() +
                        block.getMagicNumber());

        return Objects.equals(block.getHash(), hash);
    }

    /**
     * <b>IMPORTANT!</b>: this method assumes that the previous block was validated using {@link #validateBlock(Block)} method.
     * It is possible to successfully validate block pair with invalid previous block.
     * <br/>
     * At first, validates the second block and if the process was successful,
     * checks whether the hash of the previous block is equal to the field <tt>previousHash</tt> from the second block.
     * Returns the result of the last comparison.
     *
     * @param prevBlock - A previous block that is to be validated.
     * @param block - A block that is to be validated.
     * @return <tt>true</tt> if a block is valid next block in comparison to the previous block, otherwise <tt>false</tt>.
     */
    public boolean validateBlockPair(final Block prevBlock, final Block block) {
        if (isNull(prevBlock) ||  isNull(block)) return false;
        if (!validateBlock(block)) return false;

        final String hash = Blocks.applySha256(
                prevBlock.getId() +
                        prevBlock.getTimestamp() +
                        prevBlock.getPreviousHash() +
                        prevBlock.getCreatedBy() +
                        prevBlock.getMagicNumber());

        return Objects.equals(hash, block.getPreviousHash());
    }

    /**
     * A blockchain is valid when:
     * <ul>
     *     <li>there are no blocks - blockchain is empty;</li>
     *     <li>there is only one <b>valid block</b>;</li>
     *     <li>there are only <b>ordered pairs</b> of valid blocks with ordered messages.</li>
     * </ul>
     * In any other cases a blockchain is invalid.
     * <br/>
     * Glossary:
     * <ul>
     *     <li>valid block - a block that was successfully validated using {@link #validateBlock(Block)}.</li>
     *     <li>ordered pair - a pair of blocks that was successfully validated using {@link #validateBlockPair(Block, Block)}.</li>
     * </ul>
     *
     * @param blocks - A blockchain that is to be validated.
     * @return <tt>true</tt> if blockchain is valid, otherwise <tt>false</tt>.
     */
    public boolean validateBlocks(final List<Block> blocks) {
        if (blocks.isEmpty()) return true;
        if (!validateBlock(blocks.get(0))) return false;
        if (blocks.size() == 1) return true;

        for (int i = 0; i < blocks.size() - 1; i++) {
            if (!validateBlockPair(blocks.get(i), blocks.get(i + 1))) {
                return false;
            }
        }

        final List<Message> messages = blocks.stream()
                .flatMap(block -> block.getMessages().stream())
                .collect(Collectors.toList());

        return validateMessages(messages);
    }

    /**
     * Checks whether a message's id is greater than the id of the previous message
     * and returns <tt>true</tt> if the requirement was satisfied.
     *
     * @param prevMessage A previous message that is to be validated.
     * @param message A message that is to be validated.
     * @return <tt>true</tt> if a message pair is in ascending order, otherwise <tt>false</tt>.
     */
    public boolean validateMessagePair(final Message prevMessage, final Message message) {
        if (isNull(prevMessage) || isNull(message)) return false;
        return prevMessage.getId() < message.getId();
    }

    /**
     * A message list is valid when:
     * <ul>
     *     <li>there are no messages;</li>
     *     <li>ids that belong to each message are in ascending order.</li>
     * </ul>
     * In any other case, a message list is invalid.
     *
     * @param messages A message list that is to be validated.
     * @return <tt>true</tt> if a message list is valid, otherwise <tt>false</tt>
     */
    public boolean validateMessages(final List<Message> messages) {
        if (messages.isEmpty() || messages.size() == 1) return true;

        for (int i = 0; i < messages.size() - 1; i++) {
            if (!validateMessagePair(messages.get(i), messages.get(i + 1))) {
                return false;
            }
        }

        return true;
    }

    public int getNumberOfZeros() {
        return numberOfZeros;
    }

    public List<Message> getMessages() {
        synchronized (MESSAGES_LOCK) {
            return messages;
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
