package org.example.blockchain.simulation.administrator;

import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.builder.MinerBuilder;
import org.example.blockchain.simulation.Simulation;
import org.example.blockchain.simulation.administrator.builder.AdministratorBuilder;
import org.example.blockchain.simulation.builder.SimulationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class AdministratorIntegrationTest {

    @BeforeEach
    public void setUp() throws Exception {
        final Field field = BlockChain.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    public void should_stop_miners_and_reset_number_of_zeros() throws Exception {

        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(2048);

        final Simulation simulation = SimulationBuilder.builder()
                .withUsers(new ArrayList<>())
                .withFixedUserService(4)
                .withSingleThreadAdminService()
                .build();
        final BlockChain blockChain = BlockChain.getInstance();

        final Field field = BlockChain.class.getDeclaredField("numberOfZeros");
        field.setAccessible(true);
        field.set(blockChain, 10);

        final Administrator administrator = AdministratorBuilder.builder()
                .in(simulation)
                .manage(blockChain)
                .build();

        final ExecutorService minerSupplier = Executors.newSingleThreadExecutor();
        minerSupplier.submit(() -> {
            for (long i = 0; i < 2; i++) {
                simulation.submitUser(MinerBuilder.builder()
                        .withId(i)
                        .withName("Miner")
                        .withKeyPair(keyGen.generateKeyPair())
                        .withBlockChain(blockChain)
                        .withSimulation(simulation)
                        .build());
            }
        });

        simulation.submitAdministrator(administrator);

        TimeUnit.SECONDS.sleep(20);

        simulation.shutdown();
        final boolean actual = simulation.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

        assertThat(actual).isTrue();
        assertThat(blockChain.getNumberOfZeros()).isLessThan(10);

    }
}
