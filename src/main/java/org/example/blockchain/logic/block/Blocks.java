package org.example.blockchain.logic.block;

import org.example.blockchain.logic.block.builder.BlockBuilder;
import org.example.blockchain.logic.message.Message;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.nonNull;

/**
 * Contains methods for creating a valid {@link Block}.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public final class Blocks {

    /**
     * Generates a hash by applying SHA256 to the input text.
     *
     * @param input - An input text that is to be used to generate a hash.
     * @return A hash that was created by applying SHA256 to the input text.
     */
    public static String applySha256(final String input) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            final StringBuilder hexString = new StringBuilder();
            for (byte elem: hash) {
                final String hex = Integer.toHexString(0xff & elem);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Looks for a magic number by randomly selecting a value
     * and verifying if a generated hash has the required number of zeros.
     *
     * @param numberOfZeros - A required number of zeros at the beginning of a generated hash.
     * @param input - An input that is to be used to generate hash.
     * @return A magic number.
     */
    public static int findMagicNumber(final int numberOfZeros, final String input) {
        final Random random = new Random();
        int magicNumber = random.nextInt();

        String hash = applySha256(input + magicNumber);
        final String regex = "^0{" + numberOfZeros + "}[1-9a-zA-Z][\\da-zA-Z]+";

        while (!hash.matches(regex)) {
            magicNumber = random.nextInt();
            hash = applySha256(input + magicNumber);
        }

        return magicNumber;
    }

    /**
     * Carries out a process of mining a valid block.
     *
     * @param prevBlock - A previous block that will be used as a reference point when mining a new block.
     * @param messages - The list of messages that will be included in a new block.
     * @param timestamp - The timestamp at which a creator started mining the block.
     * @param createdBy - The unique identifier of a creator.
     * @return A valid block that can be added to a blockchain.
     */
    public static Block mineBlock(final Block prevBlock,
                                  final List<Message> messages,
                                  final long timestamp,
                                  final long createdBy) {

        long id = 1L;
        String previousHash = "0";
        int nProgress = 0;

        if (nonNull(prevBlock)) {
            id = prevBlock.getId() + 1L;
            previousHash = prevBlock.getHash();
            nProgress = prevBlock.getNProgress();
        }

        final long start = System.currentTimeMillis();

        final int magicNumber = findMagicNumber(nProgress, id + timestamp + previousHash + createdBy);

        final long end = System.currentTimeMillis();
        final long generationTime = (end - start) / 1000L;

        final String hash = applySha256(id + timestamp + previousHash + createdBy + magicNumber);

        return BlockBuilder.builder()
                .withId(id)
                .withTimestamp(timestamp)
                .withMagicNumber(magicNumber)
                .withGenerationTime(generationTime)
                .withHash(hash)
                .withPreviousHash(previousHash)
                .withCreatedBy(createdBy)
                .withNProgress(nProgress)
                .withMessages(prevBlock != null ? messages : new ArrayList<>())
                .build();
    }
}
