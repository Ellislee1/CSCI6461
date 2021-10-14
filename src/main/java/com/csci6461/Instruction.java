/*
 * This file implements the base instruction class for the CSCI 6461 Project computer
 */
package com.csci6461;

/**
 * This is the base Instruction class for the CSCI 6461 computer simulation.
 * It can be used directly to implement miscellaneous instructions that have no parameters to
 * decode. For derived class, the getArguments method must be implemented to decode instruction
 * parameters.
 */
public abstract class Instruction {
    /**
     * Parameter to hold instruction's name
     */
    private final String name;

    /**
     * Parameter to hold full un-decoded instruction
     */
    private short instruction;

    /**
     * Instruction class constructor that sets the instruction's name
     *
     * @param name String containing the instruction name (I.e. HLT, LDR, ADD, etc.)
     */
    Instruction(String name){
        this.name = name;
    }

    /**
     * Method to get the instruction's name (I.e. HLT, LDR, ADD, etc.)
     *
     * @return String containing the instruction's name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Method to save un-decoded instruction
     *
     * @param instruction 16-bit integer with un-decoded instruction
     */
    public void setInstruction(short instruction) {
        this.instruction = instruction;
    }

    /**
     * Method to get un-decoded instruction
     *
     * @return 16-bit int with un-decoded instruction
     */
    public short getInstruction() {
        return instruction;
    }

    /**
     * Abstract method to decode the instruction's parameters
     * @return Returns the array of arguments
     */
    abstract int[] getArguments();
}
