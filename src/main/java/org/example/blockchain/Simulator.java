package org.example.blockchain;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.logic.users.builder.UserBuilder;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Simulator {

    private static final int POOL_SIZE = 15;

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(2048);

        BlockChain blockChain = BlockChain.getInstance();

        ExecutorService minerService = Executors.newFixedThreadPool(POOL_SIZE);
        ExecutorService clientService = Executors.newFixedThreadPool(POOL_SIZE * 2);

        for (int i = 0; i < POOL_SIZE; i++) {
            clientService.submit(UserBuilder.builder()
                    .withName("Client-" + i)
                    .withKeyPair(keyGen.generateKeyPair())
                    .withBlockChain(blockChain)
                    .build()
            );

            minerService.submit(MinerBuilder.builder()
                    .withName("Miner-" + i)
                    .withKeyPair(keyGen.generateKeyPair())
                    .withBlockChain(blockChain)
                    .build()
            );
        }

        clientService.shutdown();
        clientService.awaitTermination(14, TimeUnit.SECONDS);

        minerService.shutdown();
        minerService.awaitTermination(14, TimeUnit.SECONDS);

        blockChain.getBlocks().stream().limit(15).forEach(System.out::println);
    }
}
