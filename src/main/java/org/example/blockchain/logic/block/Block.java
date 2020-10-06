package org.example.blockchain.logic.block;

import org.example.blockchain.logic.messages.Message;

import java.io.Serializable;
import java.util.List;

public class Block implements Serializable {

    private final long id;
    private final long timestamp;
    private final int magicNumber;
    private final String hash;
    private final String previousHash;
    private final long createdBy;
    private final long generationTime;
    private int nProgress;
    private final List<Message> messages;

    public Block(final long id,
                 final long timestamp,
                 final int magicNumber,
                 final String hash,
                 final String previousHash,
                 final long createdBy,
                 final long generationTime,
                 final int nProgress,
                 final List<Message> messages) {

        this.id = id;
        this.timestamp = timestamp;
        this.magicNumber = magicNumber;
        this.hash = hash;
        this.previousHash = previousHash;
        this.createdBy = createdBy;
        this.generationTime = generationTime;
        this.nProgress = nProgress;
        this.messages = messages;
    }

    @Override
    public String toString() {
        String blockData = messages.stream()
                .map(Message::toString)
                .reduce("", (all, message) -> all + "\n" + message);

        if (blockData.equals(""))
            blockData = "No transactions";

        return "Block:\n" +
                "Created by: miner" + createdBy + "\n" +
                "miner" + createdBy + " gets 100 VC\n" +
                "Id: " + id + "\n" +
                "Timestamp: " + timestamp + "\n" +
                "Magic number: " + magicNumber + "\n" +
                "Hash of the previous block: \n" + previousHash + '\n' +
                "Hash of the block: \n" + hash + "\n" +
                "Block data: " + blockData + "\n" +
                "Block was generating for " + generationTime + " seconds\n" +
                "N was was changed to " + nProgress + "\n";
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

    public void setNProgress(int nProgress) {
        this.nProgress = nProgress;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
