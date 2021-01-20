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

public class BlockChain implements Serializable {

    private static BlockChain instance;
    private int numberOfZeros;
    private final List<Message> messages;
    private final List<Block> blocks;
    private static final IdentifierStream IDENTIFIER_STREAM = new IdentifierStream();
    private static final Object MESSAGES_LOCK = new Object();

    private BlockChain() {
        numberOfZeros = 0;
        messages = new ArrayList<>();
        blocks = new ArrayList<>();
    }

    public static BlockChain getInstance() {
        if (instance == null) {
            instance = new BlockChain();
        }

        return instance;
    }

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

    public synchronized Block getLast() {
        return !blocks.isEmpty() ? blocks.get(blocks.size() - 1) : null;
    }

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

    public synchronized static int getUniqueIdentifier() {
        return IDENTIFIER_STREAM.getNext();
    }

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

    public boolean validateMessagePair(final Message prevMessage, final Message message) {
        if (isNull(prevMessage) || isNull(message)) return false;
        return prevMessage.getId() < message.getId();
    }

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
