package org.example.blockchain.simulation.builder;

import org.example.blockchain.logic.users.AbstractUser;
import org.example.blockchain.simulation.Simulation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SimulationBuilder {

    private List<AbstractUser> users;
    private ExecutorService userService;

    private SimulationBuilder() {}

    public static SimulationBuilder builder() {
        return new SimulationBuilder();
    }

    public SimulationBuilder withUsers(final List<AbstractUser> users) {
        this.users = users;
        return this;
    }

    public SimulationBuilder withUserService(final ExecutorService userService) {
        this.userService = userService;
        return this;
    }

    public SimulationBuilder withFixedThreadPool(final int nThreads) {
        this.userService = Executors.newFixedThreadPool(nThreads);
        return this;
    }

    public Simulation build() {
        return new Simulation(users, userService);
    }
}
