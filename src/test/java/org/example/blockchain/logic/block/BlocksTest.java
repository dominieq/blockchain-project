package org.example.blockchain.logic.block;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BlocksTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/apply-sha256-test.csv", numLinesToSkip = 1)
    public void should_apply_sha256(String input, String expected) {

        // when
        String actual = Blocks.applySha256(input);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/find-magic-number-test.csv", numLinesToSkip = 1)
    public void should_find_magic_number(int numberOfZeros, String input) {

        // when
        int magicNumber = Blocks.findMagicNumber(numberOfZeros, input);
        String actual = Blocks.applySha256(input + magicNumber);

        // then
        assertThat(actual).startsWith("0".repeat(numberOfZeros));
    }
}
