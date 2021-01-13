package org.example.blockchain.logic;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.block.Blocks;
import org.example.blockchain.logic.message.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (block.getHash().startsWith("0".repeat(Math.max(0, numberOfZeros)))) {
            final List<Block> newBlocks = new ArrayList<>(blocks);
            newBlocks.add(block);

            synchronized (MESSAGES_LOCK) {
                if (validate(newBlocks)) {
                    messages.removeAll(block.getMessages());

                    blocks.add(block);
                    return true;
                }
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
        boolean isIn = false;

        synchronized (MESSAGES_LOCK) {
            if (messages.isEmpty()) {
                isIn = messages.add(message);
            } else if (message.getId() > messages.get(messages.size() - 1).getId()) {
                isIn = messages.add(message);
            }
        }

        return isIn;
    }

    public synchronized static int getUniqueIdentifier() {
        return IDENTIFIER_STREAM.getNext();
    }

    public boolean validate(final List<Block> blocks) {
        for (int i = 0; i < blocks.size() - 1; i++) {
            final Block block = blocks.get(i);

            final String hash = Blocks.applySha256(
                    block.getId() +
                            block.getTimestamp() +
                            block.getPreviousHash() +
                            block.getCreatedBy() +
                            block.getMagicNumber()
            );

            if (!hash.equals(blocks.get(i + 1).getPreviousHash())) {
                return false;
            }
        }

        final List<Message> messages = blocks.stream()
                .flatMap(block -> block.getMessages().stream())
                .collect(Collectors.toList());

        for (int i = 0; i < messages.size() - 1; i++) {
            if (messages.get(i).getId() > messages.get(i + 1).getId()) {
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
            return new ArrayList<>(messages);
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
