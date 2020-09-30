package org.example.blockchain.logic.blocks;

import org.example.blockchain.logic.messages.Message;

import java.io.Serializable;
import java.util.ArrayList;

public class Block implements Serializable {

    private final long id;
    private final long timestamp;
    private final int magicNumber;
    private final String hash;
    private final String previousHash;
    private final long createdBy;
    private final long generationTime;
    private int nProgress;
    private final ArrayList<Message> messages;

    public Block(BlockBuilder blockBuilder) {
        this.id = blockBuilder.getId();
        this.timestamp = blockBuilder.getTimestamp();
        this.magicNumber = blockBuilder.getMagicNumber();
        this.generationTime = blockBuilder.getGenerationTime();
        this.hash = blockBuilder.getHash();
        this.previousHash = blockBuilder.getPreviousHash();
        this.createdBy = blockBuilder.getCreatedBy();
        this.nProgress = blockBuilder.getNProgress();
        this.messages = blockBuilder.getMessages();
    }

    @Override
    public String toString() {
        String blockData = this.getMessagesAsString();

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

    public String getMessagesAsString() {
        return messages.stream()
                .map(Message::toString)
                .reduce("", (all, message) -> all + "\n" + message);
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

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
