package org.example.blockchain.logic.block;

import org.example.blockchain.logic.block.builder.BlockBuilder;
import org.example.blockchain.logic.message.Message;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.nonNull;

public final class Blocks {

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
