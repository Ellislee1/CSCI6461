/*
 * This file implements the Miscellaneous Instruction class of the CSCI 6461 Project computer
 */
package com.csci6461;

/**
 * This class extends the Instruction abstract class for Miscellaneous Instructions.
 * These instructions to do not take any parameters so the getArguments() method returns
 * an empty array.
 */
public class MiscInstruction extends Instruction {
    /**
     * Constructor for MiscInstruction class that calls abstract class constructor
     *
     * @param name String containing instruction's name (E.g. HTL or TRAP)
     */
    MiscInstruction(String name){
        super(name);
    }
    @Override
    int[] getArguments() {
        /* Only possible argument is trap code; so extract bits 12 through 15 and return as int */
        String bits = String.format("%16s", Integer.toBinaryString(this.getInstruction())).replace(' ', '0');
        System.out.printf("[MiscInstruction::getArguments] Trap code for instruction %s is %s\n",
                bits, bits.substring(11,15));
        int trapCode = Integer.parseInt(bits.substring(11,15),2);
        System.out.printf("[MiscInstruction::getArguments] Converted trap code to binary: %d\n", trapCode);

        /* Save trap code to args array */
        int[] args = new int[1];
        args[0] = trapCode;

        /* Return args */
        return args;
    }
}
