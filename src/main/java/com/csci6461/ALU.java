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
     * Construction for the Arithmetic Logic Unit (ALU) class
     *
     * @param gpr An array of Register objects with the GPRs for the simulation
     * @param mbr Register object with the MBR for the simulation
     */
    ALU(final Register[] gpr, final Register mbr) {
        /* Allocate storage for GPRs */
        this.gpr = new Register[gpr.length];

        /* Iterate through GPR array and save GPR objects */
        System.arraycopy(gpr, 0, this.gpr, 0, gpr.length);

        /* Save MBR to local MBR parameter */
        this.mbr = mbr;
    }

    /**
     * This is the main call to process some arithmetic or logic operation
     * @param code Name of the instruction to operate
     * @param r The register of the instruction
     * @param imm The immediate value of the operation
     *
     * @return returns condition code (0-3
     */
    public int operate(final String code, final int r, final short imm) {

        return switch (code) {
            case "AMR" -> this.MemToReg(r, false);
            case "SMR" -> this.MemToReg(r, true);
            case "AIR" -> this.ImmToReg(r, false, imm);
            case "SIR" -> this.ImmToReg(r, true, imm);
            default -> -1;
        };
    }

    /**
     * Handles adding/subtracting some value from memory to a register
     * @param r The GPR to perform the addition on
     * @param subtraction Is this operation a subtraction (adding a negative number)
     * @return Returns an int with condition code
     */
    protected int MemToReg(final int r, final boolean subtraction){
        int cc = -1;
        final short operand1 = (short) this.mbr.read();
        final short operand2 = (short) this.gpr[r].read();

        if (subtraction) {
            try {
                this.gpr[r].load((short)(operand2-operand1));
            } catch (final IOException e) {
                /* TO DO: Convert to global ENUM */
                /*        Also, verify when it is appropriate to return UNDERFLOW instead */
                cc = 0;
            }
        } else {
            try {
                this.gpr[r].load((short)(operand2+operand1));
            } catch (final IOException e) {
                /* TO DO: Convert to global ENUM */
                /*        Also, verify when it is appropriate to return UNDERFLOW instead */
                cc = 0;
            }
        }
        return cc;
    }


    /**
     * Handles adding and subtracting immediate to register
     * @param r the register
     * @param imm the immediate value
     * @param subtraction is the operation is a subtraction
     * @return Returns the CC code.
     */
    protected int ImmToReg(final int r, final boolean subtraction, final short imm){
        int cc = -1;
        final short x = 31;

        final short operand2 = (short) this.gpr[r].read();

        if(operand2 == 0){
            try {
                if (subtraction) {
                    this.gpr[r].load((short) -imm);
                } else {
                    this.gpr[r].load(imm);
                }
            } catch (final IOException e){
                /* TO DO: Convert to global ENUM */
                /*        Also, verify when it is appropriate to return UNDERFLOW instead */
                cc = 0;
            }
        } else {
            try {
                if (subtraction) {
                    this.gpr[r].load((short)(operand2-imm));
                } else {
                    this.gpr[r].load((short)(operand2+imm));
                }
            }catch (final IOException e){
                /* TO DO: Convert to global ENUM */
                /*        Also, verify when it is appropriate to return UNDERFLOW instead */
                cc = 0;
            }
        }
        return cc;
    }
}
