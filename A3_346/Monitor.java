

import java.util.PriorityQueue;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	private enum STATUS {
		THINKING, HUNGRY, EATING
	};
	private int numPhilosophers;
	private STATUS[] philosopherStatus;
	private boolean philosopherIsTalking;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
        this.numPhilosophers = piNumberOfPhilosophers;
        this.philosopherStatus = new STATUS[numPhilosophers];
        this.philosopherIsTalking = false;

		// All philosophers is initially thinking so we initialize them all to THINKING state
		for (int i = 0; i < numPhilosophers; i++) {
			this.philosopherStatus[i] = STATUS.THINKING;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// ...
		int index = piTID - 1; // Adjust TID to 0-based index
		philosopherStatus[index] = STATUS.HUNGRY;
		System.out.println("Philosopher " + piTID + " is hungry and wants to pick up the chopsticks.");

		test(index);
		// Wait while unable to eat (neighbors are eating)
		while (philosopherStatus[index] != STATUS.EATING) {
			try {
				System.out.println("Philosopher " + piTID + " is waiting to eat.");
				wait(); // Wait for a putDown notification
			} catch (InterruptedException e) {
				System.err.println("Monitor.pickUp():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
		// At this point, philosopherStatus[index] == STATUS.EATING
		System.out.println("Philosopher " + piTID + " has picked up chopsticks and is EATING.");
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		// ...
        int index = piTID - 1; // Adjust TID to 0-based index

        // Change status back to thinking
        philosopherStatus[index] = STATUS.THINKING;
        System.out.println("Philosopher " + piTID + " puts down chopsticks and is THINKING.");

        // Check if neighbors can now eat
        test(getLeftNeighborIndex(index));
        test(getRightNeighborIndex(index));

        // Notify potentially waiting philosophers (including those waiting to talk)
        notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		// ...
        while (philosopherIsTalking) {
            try {
                System.out.println("Philosopher (wants to talk) is waiting because someone else is talking.");
                wait(); // Wait for endTalk notification
            } catch (InterruptedException e) {
                System.err.println("Monitor.requestTalk():");
                DiningPhilosophers.reportException(e);
                System.exit(1);
            }
        }

        // Grant permission to talk
        philosopherIsTalking = true;
        System.out.println("Philosopher granted permission to talk.");
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		// ...
        // Release permission to talk
        philosopherIsTalking = false;
        System.out.println("Philosopher finished talking.");

        // Notify potentially waiting philosophers (including those waiting to eat/talk)
        notifyAll();
	}

	// --- Some Helper Methods ---

    /**
     * Checks if philosopher at index piIndex can eat (is hungry and neighbors are not eating).
     * If so, sets their status to EATING.
     * This method does NOT notify, as notification happens in putDown/endTalk.
     */
    private void test(final int piIndex) {
        // If philosopher is hungry and both neighbors are not eating,
		// set their status to EATING
        if (philosopherStatus[piIndex] == STATUS.HUNGRY &&
            philosopherStatus[getLeftNeighborIndex(piIndex)] != STATUS.EATING &&
            philosopherStatus[getRightNeighborIndex(piIndex)] != STATUS.EATING)
        {
            philosopherStatus[piIndex] = STATUS.EATING;
            // No notify() here; the waiting thread checks its condition upon waking from notifyAll()
        }
    }

    /**
     * Calculates the array index of the left neighbor.
     */
    private int getLeftNeighborIndex(final int piIndex) {
        return (piIndex + numPhilosophers - 1) % numPhilosophers;
    }

    /**
     * Calculates the array index of the right neighbor.
     */
    private int getRightNeighborIndex(final int piIndex) {
        return (piIndex + 1) % numPhilosophers;
    }
}

// EOF
