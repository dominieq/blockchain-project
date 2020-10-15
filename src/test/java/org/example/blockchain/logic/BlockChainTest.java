package org.example.blockchain.logic;

import org.example.blockchain.logic.block.Block;
import org.example.blockchain.logic.block.builder.BlockBuilder;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.Messages;
import org.example.blockchain.logic.message.builder.SecureMessageBuilder;
import org.example.blockchain.logic.message.builder.TransactionBuilder;
import org.example.blockchain.logic.users.User;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.logic.users.builder.SenderBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockChainTest {

    private static KeyPairGenerator generator;
    private static BlockChain subject;

    @BeforeAll
    public static void initialize() throws NoSuchAlgorithmException {
        generator = KeyPairGenerator.getInstance("DSA");
        generator.initialize(2048);
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field field = BlockChain.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);

        subject = BlockChain.getInstance();
    }

    @Test
    public void should_put_valid_block_without_generation_time() {

        // given
        final Block block = getBlockForTests().build();

        // when
        subject.putLast(block);

        // then
        assertThat(subject.getBlocks()).contains(block);
    }

    @Test
    public void should_increase_number_of_zeros() {

        // given
        final Block block = getBlockForTests().build();
        final long generationTime = 59L;

        // when
        subject.putLast(block, generationTime);

        // then
        assertThat(subject.getBlocks()).contains(block);
        assertThat(subject.getNumberOfZeros()).isEqualTo(1);
    }

    @Test
    public void should_not_increase_number_of_zeros() {

        // given
        final Block block =  getBlockForTests().build();
        final long generationTime = 60L;

        // when
        subject.putLast(block, generationTime);

        // then
        assertThat(subject.getBlocks()).contains(block);
        assertThat(subject.getNumberOfZeros()).isEqualTo(0);
    }

    @Test
    public void should_add_transaction() {

        // given
        final KeyPair minerKeyPair = generator.generateKeyPair();
        final KeyPair senderKeyPair = generator.generateKeyPair();

        final String text = "Fancy text message";
        final int id = 1;
        final byte[] signature = Messages.sign(text + id, minerKeyPair.getPrivate());

        final Message secureMessage = SecureMessageBuilder.builder()
                .withId(id)
                .withText(text)
                .withSignature(signature)
                .withPublicKey(minerKeyPair.getPublic())
                .build();

        final User miner = getMinerForTests()
                .withKeyPair(minerKeyPair)
                .build();

        final User sender = getSenderForTests()
                .withKeyPair(senderKeyPair)
                .build();

        final Message message = TransactionBuilder.builder()
                .withMessage(secureMessage)
                .withFrom(miner)
                .withTo(sender)
                .withAmount(100L)
                .build();

        // when
        final Boolean isIn = subject.addMessage(message);

        // then
        assertThat(isIn).isTrue();
        assertThat(subject.getMessages()).contains(message);
    }

    @Test
    public void should_not_add_transaction() {

        // given
        final KeyPair minerKeyPair = generator.generateKeyPair();
        final KeyPair senderKeyPair = generator.generateKeyPair();

        final String text = "Fancy text message";
        final int id = 1;
        final byte[] signature = Messages.sign(text + id, minerKeyPair.getPrivate());

        final Message secureMessage = SecureMessageBuilder.builder()
                .withId(id)
                .withText(text)
                .withSignature(signature)
                .withPublicKey(minerKeyPair.getPublic())
                .build();

        final User miner = getMinerForTests()
                .withKeyPair(minerKeyPair)
                .build();

        final User sender = getSenderForTests()
                .withKeyPair(senderKeyPair)
                .build();

        final Message transaction = TransactionBuilder.builder()
                .withMessage(secureMessage)
                .withFrom(miner)
                .withTo(sender)
                .withAmount(100L)
                .build();

        // when
        final Boolean isIn1 = subject.addMessage(transaction);
        final Boolean isIn2 = subject.addMessage(transaction);

        // then
        assertThat(isIn1).isTrue();
        assertThat(isIn2).isFalse();
        assertThat(subject.getMessages()).containsOnlyOnce(transaction);
    }


    private BlockBuilder getBlockForTests() {
        return BlockBuilder.builder()
                .withId(1L)
                .withTimestamp(2L)
                .withMagicNumber(3)
                .withHash("hash")
                .withPreviousHash(null)
                .withCreatedBy(4L)
                .withGenerationTime(5L)
                .withNProgress(6)
                .withMessages(new ArrayList<>());
    }

    public MinerBuilder getMinerForTests() {
        return MinerBuilder.builder()
                .withName("TestMiner")
                .withBlockChain(subject);
    }

    public SenderBuilder getSenderForTests() {
        return SenderBuilder.builder()
                .withName("TestSender")
                .withBlockChain(subject);
    }
}
