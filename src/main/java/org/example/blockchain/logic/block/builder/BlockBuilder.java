package org.example.blockchain.logic.block.builder;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.message.Message;

import java.util.List;

public final class BlockBuilder {

    private long id;
    private long timestamp;
    private int magicNumber;
    private String hash;
    private String previousHash;
    private long createdBy;
    private long generationTime;
    private int nProgress;
    private List<Message> messages;

    private BlockBuilder() { }

    public static BlockBuilder builder() {
        return new BlockBuilder();
    }

    public BlockBuilder withId(final long id) {
        this.id = id;
        return this;
    }

    public BlockBuilder withTimestamp(final long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public BlockBuilder withMagicNumber(final int magicNumber) {
        this.magicNumber = magicNumber;
        return this;
    }

    public BlockBuilder withHash(final String hash) {
        this.hash = hash;
        return this;
    }

    public BlockBuilder withPreviousHash(final String previousHash) {
        this.previousHash = previousHash;
        return this;
    }

    public BlockBuilder withCreatedBy(final long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public BlockBuilder withGenerationTime(final long generationTime) {
        this.generationTime = generationTime;
        return this;
    }

    public BlockBuilder withNProgress(final int nProgress) {
        this.nProgress = nProgress;
        return this;
    }

    public BlockBuilder withMessages(final List<Message> messages) {
        this.messages = messages;
        return this;
    }

    public Block build() {
        return new Block(
                id,
                timestamp,
                magicNumber,
                hash,
                previousHash,
                createdBy,
                generationTime,
                nProgress,
                messages
        );
    }
}
