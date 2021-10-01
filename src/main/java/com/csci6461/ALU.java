package com.csci6461;

/**
 * This holds all processes in the ALU
 */
public class ALU {
    /**
     * This is the main call to process some arithmetic or logic operation
     * @param code The code to perform the operation on
     * @return returns some value to be stored
     */
    public short operate(short code) {
        return 0;
    }

    /**
     * Handles adding/subtracting some value from memory to a register
     * @param r The GPR to perform the addition on
     * @param effectiveAddress The precalculated effective address
     * @param subtraction Is this operation a subtraction (adding a negative number)
     * @return Returns the value to be then saved in the register
     */
    protected short MemToReg(int r, short effectiveAddress, boolean subtraction){
        return 0;
    }
}
