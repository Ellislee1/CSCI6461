/*
 * This file implements the vector processor for the simple CSCI6461 simulation
 */
package com.csci6461;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * This class extends ControlUnit to add support for pipelined vector operations.
 * All scalar operations are handled by the base class methods but any vector instructions
 * are handled by methods that simulate functional units to process overlapped vector
 * instructions as a simple pipeline.
 */
public class VectorProcessor extends ControlUnit {
    /**
     * Initialize static configuration variables
     */
    private static final int VECTOR_PIPELINE_TIMEOUT = 100; // Set clock timeout in ms
    /**
     * Parameter to hold Clock class to synchronize vector pipeline
     */
    private Clock clock;
    /**
     * Parameter to hold input and output queues between pipeline stages
     */
    private Queue<Short[]> inputQ;
    private Queue<Short> outputQ;
    /**
     * Parameter to hold vector load/store functional unit
     */
    private VectorLoadStoreUnit loadStoreUnit;
    /**
     * Parameter to hold vector adder
     */
    private VectorAdder vectorAdder;
    /**
     * Overload base Control Unit constructor to initialize vector pipeline
     *
     * @param txtInput Object to retrieve user text input
     * @param btnInput Button object to indicate completion of user input
     * @param lblInput Label GUI object to enable user input on IN opcode
     * @param output   Output text object to simulate console output
     */
    public VectorProcessor(TextField txtInput, Button btnInput, Label lblInput, ArrayList<Integer> output) {
        /* Call superclass constructor */
        super(txtInput, btnInput, lblInput, output);

        /* Instantiate and initialize clock */
        clock = new Clock(VECTOR_PIPELINE_TIMEOUT);

        /* Allocate input and output queues */
        inputQ = new LinkedList<>();
        outputQ = new LinkedList<>();

        /* Allocate the vector load/store functional unit */
        loadStoreUnit = new VectorLoadStoreUnit(clock,mainMemory.getData(),outputQ,inputQ);

        /* Allocate the vector adder unit */
        vectorAdder = new VectorAdder(clock,inputQ,outputQ);
    }

    /**
     * This method starts the vector processing pipeline whenever a vector operation is received
     *
     * @param decodedInstruction Object with decoded instruction so we can get arguments
     * @param subtract Boolean set to true if this is a subtract operation
     */
    private void runPipeline(Instruction decodedInstruction, boolean subtract) throws IOException {
        final int args[];
        short length;
        short v1address;
        short v2address;

        /* Get arguments from decoded instruction */
        args = decodedInstruction.getArguments();
        System.out.printf("[VectorProcessor::runPipeline] Have vector instruction arguments: r = %d, ix %d, i = %d, address = %d\n",
                args[0], args[1], args[2], args[3]);

        /* Get vector length from register indicated in arg[0] */
        length = gpr[args[0]].read();
        System.out.printf("[VectorProcessor::runPipeline] Vector length is %d\n", length);

        /* Calculate EA from instruction arguments and load vector addresses from memory */
        short ea = calculateEA(args[3],args[1],args[2]);
        System.out.printf("[VectorProcessor::runPipeline] Have EA for vector instruction: %d\n", ea);
        v1address = loadDataFromMemory(ea);
        v2address = loadDataFromMemory(ea + 1);
        System.out.printf("[VectorProcessor::runPipeline] Vector 1 address is %d and vector 2 address is %d\n",
                v1address, v2address);

        /* Initialize load/store stage with vector addresses and length */
        loadStoreUnit.init(v1address, v2address, length);

        /* Create a new thread for load/store pipeline */
        Thread loadStoreThread = new Thread(loadStoreUnit);

        /* Set adder stage to appropriate stage */
        vectorAdder.setSubtract(subtract);

        /* Create a new thread for adder stage */
        Thread adderThread = new Thread(vectorAdder);

        /* Start pipeline */
        loadStoreThread.start();
        adderThread.start();

        /* Wait for pipeline complete */
        boolean bContinue = true;
        do {
            if (loadStoreUnit.getComplete()) {
                /* Stop adder thread */
                vectorAdder.doComplete();

                bContinue = false;
            }
            try {
                clock.waitForNextTick();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (bContinue);
    }

    /**
     * This method overrides the base class implementation to support pipelined vector instructions.
     *
     * @param decodedInstruction Instruction object of appropriate type to get arguments
     *
     * @return Boolean to indicate whether program execution continues or not
     *
     * @throws IOException Whenever an invalid input is received
     */
    @Override
    public boolean processInstruction(Instruction decodedInstruction) throws IOException {

        /* Get the instruction's name so we can process accordingly */
        final String name = decodedInstruction.getName();

        /* Check if instruction is a vector opcode */
        if (Objects.equals(name, "VADD")) {
            System.out.println("[VectorProcessor::processInstruction] Received Vector Add Opcode!");
            runPipeline(decodedInstruction,false);
        } else if (Objects.equals(name, "VSUB")) {
            System.out.println("[VectorProcessor::processInstruction] Received Vector Add Opcode!");
            runPipeline(decodedInstruction,true);
        }

        /* Pass scalar instruction to superclass for processing */
        return (super.processInstruction(decodedInstruction));
    }
}
