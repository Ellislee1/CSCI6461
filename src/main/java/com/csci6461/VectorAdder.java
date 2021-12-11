/**
 * This file implements the vector adder module for the simple CSCI6461 computer
 */

package com.csci6461;

import java.util.Queue;

public class VectorAdder implements Runnable {
    /**
     * Constant for delay in arithmetic operations to sequence pipeline properly
     */
    private static final int STARTUP_TIME = 1;
    private static final int DELAY = 1;
    /**
     * Parameter to hold Clock object to synchronize pipeline
     */
    private Clock clock;

    /**
     * Parameter to hold input queue of operands
     */
    private Queue<Short[]> inputQ;

    /**
     * Parameter to hold output queue for results
     */
    private Queue<Short> outputQ;

    /**
     * Parameter to hold add/subtract flag
     */
    private boolean subtract;

    /**
     * Parameter to signal stop thread
     */
    private boolean complete;

    /**
     * Default constructor to initialize clock and input/output queues
     *
     * @param clock   Clock object to synchronize pipeline
     * @param inputQ  Queue object where operands will be placed
     * @param outputQ Queue object to put results
     */
    public VectorAdder(Clock clock, Queue<Short[]> inputQ, Queue<Short> outputQ) {
        this.clock = clock;
        this.inputQ = inputQ;
        this.outputQ = outputQ;

        /* Initialize operation to add */
        this.subtract = false;
    }

    /**
     * Method to set add/subtract flag
     *
     * @param value Boolean set to true for subtract and false for add
     */
    public void setSubtract(boolean value) {
        this.subtract = value;
    }

    /**
     * Method to signal pipeline completion
     */
    public void doComplete() {
        complete = true;
    }

    /**
     * Overrides base class run method to implement vector operation execute stage
     */
    @Override
    public void run() {
        int cycles = 0;
        int opCounter = -1;
        complete = false;
        Short[] input = null;
        short result = 0;

        while (!complete) {
            try {
                clock.waitForNextTick();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cycles++;

//            System.out.printf("Started execution stage for cycle %d. Op counter is %d\n",
//                    cycles, opCounter);
            /* Wait until previous stage is primed before starting execution  */
            if (cycles > STARTUP_TIME) {

                if (opCounter > 0) {
                    if (subtract) {
                        System.out.printf("Subtract %d from %d in progress at cycle %d.\n", input[0], input[1], cycles);
                    } else {
                        System.out.printf("Add %d to %d at in progress cycle %d.\n", input[0], input[1], cycles);
                    }
                    opCounter--;
                } else {

                    /* Send any pending results to output queue */
                    if (input != null) {
                        /**
                         * TO DO: Check for overflow?
                         */
                        /* Put result on output queue */
                        System.out.printf("Execution stage pushing result of %d at cycle %d\n", result, cycles);
                        outputQ.add(result);
                        input = null;
                    }

                    /* If input not empty, start next add/subtract */
                    if (!inputQ.isEmpty()) {
                        input = inputQ.remove();
                        if (subtract) {
                            System.out.printf("Begin subtract %d from %d at cycle %d.\n", input[0], input[1], cycles);
                            result = (short) (input[0] - input[1]);
                        } else {
                            System.out.printf("Begin add %d to %d at cycle %d.\n", input[0], input[1], cycles);
                            result = (short) (input[0] + input[1]);
                        }
                        opCounter = DELAY;

                    } else {
                        /* Input Queue is empty! Stall... */
                        System.out.printf("Vector pipeline stall on execute stage at cycle %d\n", cycles);
                    }
                }
            }
        }
    }
}
