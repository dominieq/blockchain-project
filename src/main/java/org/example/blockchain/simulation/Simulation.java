package org.example.blockchain.simulation;

import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.builder.TransactionBuilder;
import org.example.blockchain.logic.users.AbstractUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.isNull;

public class Simulation {

    private final List<AbstractUser> users;
    private final ExecutorService userService;

    public Simulation(final List<AbstractUser> users,
                      final ExecutorService userService) {

        this.users = users;
        this.userService = userService;
    }

    public synchronized void createAndPerformTransaction(final AbstractUser user) {
        if (isNull(user)) return;

        final List<AbstractUser> usersCopy = new ArrayList<>(users);
        usersCopy.remove(user);
        if (usersCopy.isEmpty()) return;

        final int index = new Random().nextInt(usersCopy.size());
        final AbstractUser chosenUser = usersCopy.get(index);

        if (user.getCoins() == 0) return;
        final int chosenCoins = new Random().nextInt(user.getCoins()) + 1;

        final Message transaction = TransactionBuilder.builder()
                .withFrom(user)
                .withTo(chosenUser)
                .withAmount(chosenCoins)
                .withMessage(user.prepareMessage())
                .build();

        if (user.getBlockChain().addMessage(transaction)) {
            chosenUser.addCoins(chosenCoins);
            user.takeCoins(chosenCoins);
        }
    }

    public void shutdown() {
        users.forEach(AbstractUser::terminate);
    }

    public void shutdownNow() {
        userService.shutdownNow();
    }

    public List<AbstractUser> getUsers() {
        return users;
    }

    public ExecutorService getUserService() {
        return userService;
    }
}
