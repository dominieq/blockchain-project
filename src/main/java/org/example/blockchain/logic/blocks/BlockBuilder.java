package org.example.blockchain.logic.blocks;

import org.example.blockchain.logic.messages.Message;

import java.util.ArrayList;

public class BlockBuilder {

    private long id;
    private long timestamp;
    private int magicNumber;
    private long generationTime;
    private String hash;
    private String previousHash;
    private long createdBy;
    private int nProgress;
    private ArrayList<Message> messages;

    public Block build() {
        return new Block(this);
    }

    public BlockBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public BlockBuilder withTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public BlockBuilder withMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
        return this;
    }

    public BlockBuilder withGenerationTime(long generationTime) {
        this.generationTime = generationTime;
        return this;
    }

    public BlockBuilder withHash(String hash) {
        this.hash = hash;
        return this;
    }

    public BlockBuilder withPreviousHash(String previousHash) {
        this.previousHash = previousHash;
        return this;
    }

    public BlockBuilder withCreatedBy(long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public BlockBuilder withNProgress(int nProgress) {
        this.nProgress = nProgress;
        return this;
    }

    public BlockBuilder withMessages(ArrayList<Message> messages) {
        this.messages = messages;
        return this;
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public int getNProgress() {
        return nProgress;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
