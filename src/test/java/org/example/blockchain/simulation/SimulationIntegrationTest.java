package org.example.blockchain.simulation;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.logic.users.builder.SimpleUserBuilder;
import org.example.blockchain.simulation.builder.SimulationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class SimulationIntegrationTest {

    @BeforeEach
    public void setUp() throws Exception {
        final Field field = BlockChain.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    public void should_gracefully_shut_down_simulation() throws Exception {

        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(2048);

        final Simulation simulation = SimulationBuilder.builder()
                .withUsers(new ArrayList<>())
                .withFixedUserService(8)
                .withSingleThreadAdminService()
                .build();
        final BlockChain blockChain = BlockChain.getInstance();

        final ExecutorService minerSupplier = Executors.newSingleThreadExecutor();
        final ExecutorService userSupplier = Executors.newSingleThreadExecutor();

        minerSupplier.submit(() -> {
            for (int i = 0; i < 2; i++) {
                simulation.submitUser(MinerBuilder.builder()
                        .withName("Miner-" + i)
                        .withKeyPair(keyGen.generateKeyPair())
                        .withBlockChain(blockChain)
                        .withSimulation(simulation)
                        .build());
            }
        });

        userSupplier.submit(() -> {
            for (int i = 0; i < 2; i++) {
                simulation.submitUser(SimpleUserBuilder.builder()
                        .withName("Client-" + i)
                        .withKeyPair(keyGen.generateKeyPair())
                        .withBlockChain(blockChain)
                        .withSimulation(simulation)
                        .build());
            }
        });

        TimeUnit.SECONDS.sleep(15);

        simulation.shutdown();
        final boolean actual = simulation.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

        assertThat(actual).isTrue();
        simulation.users.forEach(user -> {
            assertThat(user.isActive()).isFalse();
            assertThat(user.isTerminated()).isTrue();
        });
    }
}
