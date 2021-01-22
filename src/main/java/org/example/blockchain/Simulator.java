package org.example.blockchain;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.logic.users.builder.UserBuilder;
import org.example.blockchain.simulation.Simulation;
import org.example.blockchain.simulation.builder.SimulationBuilder;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulator {

    private static final int POOL_SIZE = 100;
    private static final int INITIAL_MINERS_COUNT = 15;
    private static final int INITIAL_USERS_COUNT = 30;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(2048);

        final Simulation simulation = SimulationBuilder.builder()
                .withUsers(new ArrayList<>())
                .withFixedThreadPool(POOL_SIZE)
                .build();
        final BlockChain blockChain = BlockChain.getInstance();

        final ExecutorService minerSupplier = Executors.newSingleThreadExecutor();
        final ExecutorService userSupplier = Executors.newSingleThreadExecutor();

        minerSupplier.submit(() -> {
            for (int i = 0; i < INITIAL_MINERS_COUNT; i++) {
                simulation.submitUser(MinerBuilder.builder()
                        .withName("Miner-" + i)
                        .withKeyPair(keyGen.generateKeyPair())
                        .withBlockChain(blockChain)
                        .withSimulation(simulation)
                        .build());
            }
        });

        userSupplier.submit(() -> {
            for (int i = 0; i < INITIAL_USERS_COUNT * 2; i++) {
                simulation.submitUser(UserBuilder.builder()
                        .withName("Client-" + i)
                        .withKeyPair(keyGen.generateKeyPair())
                        .withBlockChain(blockChain)
                        .withSimulation(simulation)
                        .build());
            }
        });
    }
}
