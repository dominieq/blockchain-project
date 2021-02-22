package org.example.blockchain.simulation.administrator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.blockchain.logic.BlockChain;
import org.example.blockchain.logic.users.Miner;
import org.example.blockchain.simulation.Simulation;

/**
 * Takes care of a {@link BlockChain} in a certain {@link Simulation} by
 * managing required number of zeros at the beginning of hashes.
 *
 * @author Dominik Szmyt
 * @since 1.1.0
 */
public class Administrator implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Administrator.class);

    /**
     * A value indicating that only a one block was added to a {@link BlockChain} in the given amount of time.
     * An {@code Administrator} is going to increase it's stagnancy meter till it exceeds its threshold.
     */
    private static final double SLOW_GROWTH_SPEED = 1;

    /**
     * The maximum number of times only a one block was added to a {@link BlockChain} in the given amount of time.
     * If a {@link #stagnancy} meter reaches this value, an {@code Administrator} decreases number of zeros
     * from its {@link BlockChain} by half.
     */
    private static final int STAGNANCY_THRESHOLD = 5;

    private final Simulation simulation;
    private final BlockChain blockchain;
    private int prevBlockchainSize;
    private int stagnancy;

    /**
     * Create an {@code Administrator} with all necessary fields.
     * @param simulation A {@link Simulation} in which an {@code Administrator} is to operate.
     * @param blockchain A {@link BlockChain} that is to be controlled by an {@code Administrator}.
     */
    public Administrator(final Simulation simulation,
                          final BlockChain blockchain) {

        this.simulation = simulation;
        this.blockchain = blockchain;
        prevBlockchainSize = blockchain.size();
    }

    /**
     * The main purpose of an {@code Administrator} is to control the number of zeros of its {@link BlockChain}.
     * To accomplish this task, an {@code Administrator} checks whether there was any progress in adding new blocks
     * or if this progress wasn't satisfying enough.
     * <br>
     * If there are no new blocks after the timeout exceeded,
     * it means that miners are probably stuck finding magic number.
     * An {@code Administrator} tells miners to stop looking for it and resets number of zeros to its default value.
     * <br>
     * If the progress wasn't satisfying enough, an {@code Administrator} increments theirs {@link #stagnancy} meter.
     * If the stagnancy meter reaches a certain threshold, an {@code Administrator} decreases number of zeros by half.
     */
    @Override
    public void run() {
        final int currentBlockchainSize = blockchain.size();

        if (currentBlockchainSize == prevBlockchainSize) {
            LOGGER.info("No progress registered. Stopping miners and resetting number of zeros...");

            simulation.getUsers().stream()
                    .filter(user -> user instanceof Miner)
                    .map(user -> (Miner) user)
                    .parallel()
                    .forEach(Miner::stopMining);

            blockchain.setNumberOfZeros(0);
            LOGGER.info("Number of zeros reset to 0.");
        } else if (currentBlockchainSize - prevBlockchainSize == SLOW_GROWTH_SPEED) {
            LOGGER.info("Growth speed not satisfying enough. Increasing stagnancy...");

            if (++stagnancy >= STAGNANCY_THRESHOLD) {
                LOGGER.info("Stagnancy meter reached its threshold. Decreasing number of zeros by half...");

                final int oldNumber = blockchain.getNumberOfZeros();
                blockchain.setNumberOfZeros(oldNumber / 2);
                LOGGER.info("Number of zeros changed to {}", oldNumber / 2);
            }
        }

        prevBlockchainSize = currentBlockchainSize;
    }

    public int getPrevBlockchainSize() {
        return prevBlockchainSize;
    }

    public int getStagnancy() {
        return stagnancy;
    }
}
