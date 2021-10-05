package com.csci6461;

import java.io.IOException;

/**
 * This holds all processes in the ALU
 */
public class ALU {
    /**
     * Parameter to hold the General Purpose Registers (GPR)
     */
    public Register[] gpr;

    /**
     * Parameter to hold the Memory Buffer Register (MBR)
     */
    public Register mbr;

    /**
     * Enumeration for condition codes: cc(0), cc(1), cc(2), and cc(3)
     */
    public enum CC {
        OVERFLOW, UNDERFLOW, DIVZERO, EQUALORNOT
    }

    /**
     * Construction for the Arithmetic Logic Unit (ALU) class
     *
     * @param gpr An array of Register objects with the GPRs for the simulation
     * @param mbr Register object with the MBR for the simulation
     */
    ALU(Register[] gpr, Register mbr) {
        /* Allocate storage for GPRs */
        this.gpr = new Register[gpr.length];

        /* Iterate through GPR array and save GPR objects */
        for (int i = 0; i < gpr.length; i++) {
            this.gpr[i] = gpr[i];
        }

        /* Save MBR to local MBR parameter */
        this.mbr = mbr;
    }
    /**
     * This is the main call to process some arithmetic or logic operation
     * @param code The code to perform the operation on
     * @return returns some value to be stored
     */
    public short operate(short code) {
        return 0;
    }

    /**
     * This is the main call to process some arithmetic or logic operation
     * @param code Name of the instruction to operate
     *
     * @return returns condition code (0-3
     */
    public int operate(String code, int r) {
        int cc = -1;

        switch (code) {
            case "AMR":
                cc = MemToReg(r,false);
                break;
            case "SMR":
                cc = MemToReg(r,true);
                break;
        }
        return cc;
    }

    /**
     * Handles adding/subtracting some value from memory to a register
     * @param r The GPR to perform the addition on
     * @param subtraction Is this operation a subtraction (adding a negative number)
     * @return Returns an int with condition code
     */
    protected int MemToReg(int r, boolean subtraction){
        int cc = -1;
        short operand1 = (short) mbr.read();
        short operand2 = (short) gpr[r].read();

        if (subtraction) {
            try {
                gpr[r].load((short)(operand2-operand1));
            } catch (IOException e) {
                /* TO DO: Convert to global ENUM */
                /*        Also, verify when it is appropriate to return UNDERFLOW instead */
                cc = 0;
            }
        } else {
            try {
                gpr[r].load((short)(operand2+operand1));
            } catch (IOException e) {
                /* TO DO: Convert to global ENUM */
                /*        Also, verify when it is appropriate to return UNDERFLOW instead */
                cc = 0;
            }
        }
        return cc;
    }
}
