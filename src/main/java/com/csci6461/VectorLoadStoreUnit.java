/**
 * This file implements the vector load-store processing unit for the CSCI 6461 simple computer
 */

package com.csci6461;

import java.util.Queue;

public class VectorLoadStoreUnit implements Runnable {
    /**
     * Parameter to hold Clock object to synchronize pipeline
     */
    private Clock clock;

    /**
     * Parameter to hold storage for memory data array
     * NOTE: Vector processor is assumed to have 2 independent connections into memory;
     *       therefore, we write directly into the memory data array. The vector processor
     *       avoids hazards by operating in one vector slot at a time.
     */
    private short[] data;

    /**
     * Parameter to hold input queue to retrieve items waiting for store
     */
    private Queue<Short> inputQ;

    /**
     * Parameter to hold output queue to put items after load
     */
    private Queue<Short[]> outputQ;

    /**
     * Parameter to hold input and output counters
     */
    private int inputCount;
    private int outputCount;

    /**
     * Parameter to hold starting addresses for each vector
     */
    private short v1address;
    private short v2address;

    /**
     * Parameter to hold the length of the vectors
     */
    private int length;

    /**
     * Parameter to hold pipeline status
     */
    private boolean complete;

    /**
     * This is the default constructor for the
     * @param clock Clock object to synchronize queue
     * @param memory Memory storage array
     * @param inputQ Queue object to put items waiting for store
     * @param outputQ Queue object to put items after load
     */
    public VectorLoadStoreUnit (Clock clock, short[] memory, Queue<Short> inputQ, Queue<Short[]> outputQ) {
        this.clock = clock;
        this.data = memory;
        this.inputQ = inputQ;
        this.outputQ = outputQ;

        /* Initialize length and continue to prevent lock-up if trying to execute without initializing */
        length = 0;
        complete = true;
    }

    /**
     * This method initializes a new pipeline execution
     *
     * @param v1address Address of first vector
     * @param v2address Address of second vector
     * @param length Length of vectors
     */
    public void init (short v1address, short v2address, int length) {
        complete = false;
        inputCount = 0;
        outputCount = 0;
        this.v1address = v1address;
        this.v2address = v2address;
        this.length = length;

        /* Make sure queues are empty */
        if (!inputQ.isEmpty()) {
            inputQ.clear();
        }
        if(!outputQ.isEmpty()) {
            outputQ.clear();
        }
    }

    /**
     * Method to check pipeline completion status
     *
     * @return true if pipeline is complete, false otherwise
     */
    public boolean getComplete() {
        return (complete);
    }

    /**
     * Overrides the base class run method to start the input/output pipeline
     */
    @Override
    public void run() {
        int cycles = 0;

        while (inputCount < length) {
            /* Wait for next clock tick */
            try {
                clock.waitForNextTick();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cycles++;

            /* We have two concurrent connections to memory, so we can either read or write during one cycle */
            /* Check for data to write at every other cycle in the pipeline */
            /* Pipeline execution ends once the output vector is complete */
            if ((cycles % 2 == 0) && (!inputQ.isEmpty())) {
                Short output = inputQ.remove();
                short address = (short) (v1address + inputCount);
                System.out.printf("Writing %d to memory address %d at cycle %d\n", output, address, cycles);
                data[address] = output;
                inputCount++;
            } else if (outputCount < length) {
                Short[] input = new Short[2];
                input[0] = data[v1address + outputCount];
                input[1] = data[v2address + outputCount];
                System.out.printf("Read %d from vector 1 and %d from vector 2 at cycle %d\n", input[0], input[1], cycles);
                outputQ.add(input);
                outputCount++;
            } else {
                /* Nothing to read or write! Stall... */
                System.out.printf("Vector pipeline stall on input/output at cycle %d\n", cycles);
            }
        }
        complete = true;
    }
}
