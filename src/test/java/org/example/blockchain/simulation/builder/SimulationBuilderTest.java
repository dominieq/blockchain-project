package org.example.blockchain.simulation.builder;

import org.example.blockchain.logic.users.AbstractUser;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

public class SimulationBuilderTest {

    private SimulationBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = SimulationBuilder.builder();
    }

    @Test
    public void should_build_simulation_with_custom_executor_service() {

        // given
        final List<AbstractUser> users = new ArrayList<>();
        final ExecutorService userService = Executors.newSingleThreadExecutor();

        // when
        final Simulation actual = subject
                .withUsers(users)
                .withUserService(userService)
                .build();

        // then
        assertThat(actual)
                .hasFieldOrPropertyWithValue("users", users)
                .hasFieldOrPropertyWithValue("userService", userService);
    }

    @Test
    public void should_build_simulation_with_default_executor_service() {

        // given
        final List<AbstractUser> users = new ArrayList<>();

        // when
        final Simulation actual = subject
                .withUsers(users)
                .withFixedThreadPool(5)
                .build();

        // then
        assertThat(actual).hasFieldOrPropertyWithValue("users", users);
        assertThat(actual.getUserService()).isInstanceOf(ThreadPoolExecutor.class);
    }
}

