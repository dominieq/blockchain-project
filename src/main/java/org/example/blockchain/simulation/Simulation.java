package org.example.blockchain.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.blockchain.logic.message.Message;
import org.example.blockchain.logic.message.builder.TransactionBuilder;
import org.example.blockchain.logic.users.AbstractUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

/**
 * Represents a real world where miners and users can utilize a blockchain
 * by mining new blocks and performing transactions.
 *
 * @author Dominik Szmyt
 * @since 1.0.0
 */
public class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);
    private final List<AbstractUser> users;
    private final ExecutorService userService;
    private CountDownLatch countDownLatch;

    /**
     * Create a {@code Simulation} with all needed fields.
     * @param users A list of users that will participate in a simulation.
     * @param userService A service that will manage threads.
     */
    public Simulation(final List<AbstractUser> users,
                      final ExecutorService userService) {

        this.users = users;
        this.userService = userService;
    }

    /**
     * At first, selects another random user that is going to be a recipient in a new transaction.
     * Then, chooses a random number of coins that is to be transferred from the sender to the recipient.
     * In the end, tries to add a transaction to a blockchain.
     * If the addition was successful, the transaction is deemed completed
     * and coins are transferred from one user to another.
     *
     * @param user A user that wants to perform a transaction.
     */
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

    /**
     * Adds user to the list of all users and then starts user's thread.
     * @param user A user that is to be submitted to the {@code Simulation}.
     */
    public synchronized void submitUser(final AbstractUser user) {
        users.add(user);
        userService.submit(user);
    }

    /**
     * Gracefully stops each user.
     */
    public synchronized void shutdown() {
        final ExecutorService service = Executors.newFixedThreadPool(users.size());
        countDownLatch = new CountDownLatch(users.size());

        users.forEach(user -> service.submit(() -> {
            LOGGER.info("Stopping {}...", user);
            user.terminate();

            try {
                while(!user.isTerminated()) TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
                LOGGER.warn("{} was interrupted.", user);
            } finally {
                countDownLatch.countDown();
            }
        }));
    }

    /**
     * Should be fired after {@link #shutdown()} method was used. Awaits the termination of all users.
     *
     * @param timeout The maximum time to wait.
     * @param timeUnit The time unit of the {@code timeout} argument.
     * @return {@code true} if all users were terminated and {@code false} if the waiting time elapsed before that happened.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @since 1.1.0
     */
    public boolean awaitTermination(final long timeout, final TimeUnit timeUnit)
            throws InterruptedException {

        return countDownLatch.await(timeout, timeUnit);
    }

    /**
     * Shuts down each user's thread.
     */
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
