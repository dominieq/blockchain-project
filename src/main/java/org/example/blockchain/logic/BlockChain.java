package org.example.blockchain.logic;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.block.Blocks;
import org.example.blockchain.logic.messages.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockChain implements Serializable {

    private static BlockChain instance;
    private int numberOfZeros;
    private final ArrayList<Message> messages;
    private final ArrayList<Block> blocks;
    private static final IdentifierStream IDENTIFIER_STREAM = new IdentifierStream();
    private static final Object MESSAGES_LOCK = new Object();

    private BlockChain() {
        this.numberOfZeros = 0;
        this.messages = new ArrayList<>();
        this.blocks = new ArrayList<>();
    }

    public static BlockChain getInstance() {
        if (instance == null) {
            instance = new BlockChain();
        }

        return instance;
    }

    public synchronized boolean putLast(Block block) {
        if (block.getHash().startsWith("0".repeat(Math.max(0, this.numberOfZeros)))) {
            ArrayList<Block> newBlocks = new ArrayList<>(this.blocks);
            newBlocks.add(block);

            synchronized (MESSAGES_LOCK) {
                if (validate(newBlocks)) {
                    this.messages.removeAll(block.getMessages());

                    this.blocks.add(block);
                    return true;
                }
            }
        }

        return false;
    }

    public synchronized boolean putLast(Block block, long generationTime) {
        boolean isIn = this.putLast(block);

        if (isIn) {
            if (generationTime < 60L) {
                if (this.numberOfZeros < 3) {
                    block.setNProgress(++this.numberOfZeros);
                }
            } else if (this.numberOfZeros > 0) {
                block.setNProgress(--this.numberOfZeros);
            }
        }

        return isIn;
    }

    public synchronized Block getLast() {
        return !this.blocks.isEmpty() ? this.blocks.get(this.blocks.size() - 1) : null;
    }

    public boolean addMessage(Message message) {
        boolean isIn = false;

        synchronized (MESSAGES_LOCK) {
            if (this.messages.isEmpty()) {
                isIn = this.messages.add(message);
            } else if (message.getId() > this.messages.get(this.messages.size() - 1).getId()) {
                isIn = this.messages.add(message);
            }
        }

        return isIn;
    }

    public synchronized static int getUniqueIdentifier() {
        return IDENTIFIER_STREAM.getNext();
    }

    public boolean validate(ArrayList<Block> blocks) {
        for (int i = 0; i < blocks.size() - 1; i++) {
            Block block = blocks.get(i);

            String hash = Blocks.applySha256(
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

        List<Message> messages = blocks.stream()
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

    public ArrayList<Message> getMessages() {
        synchronized (MESSAGES_LOCK) {
            return new ArrayList<>(this.messages);
        }
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }
}
