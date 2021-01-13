package org.example.blockchain.logic.block;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

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

    public static int findMagicNumber(int numberOfZeros, String input) {
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
}
