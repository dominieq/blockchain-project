package org.example.blockchain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.logic.users.builder.SimpleUserBuilder;
import org.example.blockchain.simulation.Simulation;
import org.example.blockchain.simulation.administrator.Administrator;
import org.example.blockchain.simulation.administrator.builder.AdministratorBuilder;
import org.example.blockchain.simulation.builder.SimulationBuilder;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Starts simulation with 15 miners and 30 simple users.
 * Miners and users are submitted concurrently by two threads.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class SimulationStarter {

    private static final Logger LOGGER = LogManager.getLogger(SimulationStarter.class);
    private static final int POOL_SIZE = 100;
    private static final long INITIAL_MINERS_COUNT = 15;
    private static final long INITIAL_USERS_COUNT = 30;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(2048);

        final Simulation simulation = SimulationBuilder.builder()
                .withUsers(new ArrayList<>())
                .withFixedUserService(POOL_SIZE)
                .withSingleThreadAdminService()
                .build();
        final BlockChain blockChain = BlockChain.getInstance();

        final Administrator administrator = AdministratorBuilder.builder()
                .in(simulation)
                .manage(blockChain)
                .build();

        final ExecutorService minerSupplier = Executors.newSingleThreadExecutor();
        final ExecutorService userSupplier = Executors.newSingleThreadExecutor();

        minerSupplier.submit(() -> {
            for (long i = 0; i < INITIAL_MINERS_COUNT; i++) {
                simulation.submitUser(MinerBuilder.builder()
                        .withId(i)
                        .withName("Miner")
                        .withKeyPair(keyGen.generateKeyPair())
                        .withBlockChain(blockChain)
                        .withSimulation(simulation)
                        .build());
            }
        });

        userSupplier.submit(() -> {
            for (long i = 0; i < INITIAL_USERS_COUNT * 2; i++) {
                simulation.submitUser(SimpleUserBuilder.builder()
                        .withId(i)
                        .withName("Client")
                        .withKeyPair(keyGen.generateKeyPair())
                        .withBlockChain(blockChain)
                        .withSimulation(simulation)
                        .build());
            }
        });

        simulation.submitAdministrator(administrator);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Gracefully shutting down application...");
            simulation.shutdown();
            boolean isTerminated = false;

            try {
                isTerminated = simulation.awaitTermination(5L, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                LOGGER.warn("Graceful shutdown was interrupted.");
            } finally {
                if (!isTerminated) simulation.shutdownNow();
            }
        }));
    }
}
