package org.example.blockchain.simulation.builder;

import org.example.blockchain.logic.users.AbstractUser;
import org.example.blockchain.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SimulationBuilderTest {

    private SimulationBuilder subject;

    @BeforeEach
    public void setUp() {
        subject = SimulationBuilder.builder();
    }

    @Test
    public void should_build_simulation_with_custom_values() {

        // given
        final List<AbstractUser> users = new ArrayList<>();
        final ExecutorService userService = mock(ExecutorService.class);
        final ScheduledExecutorService adminService = mock(ScheduledExecutorService.class);

        // when
        final Simulation actual = subject
                .withUsers(users)
                .withUserService(userService)
                .withAdminService(adminService)
                .build();

        // then
        assertThat(actual)
                .hasFieldOrPropertyWithValue("users", users)
                .hasFieldOrPropertyWithValue("userService", userService)
                .hasFieldOrPropertyWithValue("adminService", adminService);
    }

    @Test
    public void should_build_simulation_with_default_user_service() {

        // when
        final Simulation actual = subject
                .withFixedUserService(5)
                .build();

        // then
        assertThat(actual.getUserService()).isInstanceOf(ThreadPoolExecutor.class);
    }

    @Test
    public void should_build_simulation_with_default_admin_service() {

        // when
        final Simulation actual = subject
                .withSingleThreadAdminService()
                .build();

        // then
        assertThat(actual.getAdminService()).isInstanceOf(ScheduledExecutorService.class);
    }
}

