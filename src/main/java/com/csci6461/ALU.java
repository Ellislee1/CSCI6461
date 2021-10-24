package com.csci6461;

import java.io.IOException;
import java.io.OutputStream;

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
     * This method checks the result of addition or subtraction and returns the appropriate
     * Condition Code if overflow or underflow occured
     *
     * @param operand1 Short with value of first operand of add or subtract operation
     * @param operand2 Short with value of second operand of add or subtract operation
     * @param result Short with result of add or subtract operation
     *
     * @return Returns a CC enumerator object set to CC.OKAY if no error condition was
     *         detected or CC.OVERFLOW or CC.UNDERFLOW if overflow/underflow occurred
     */
    private CC getAddSubtractConditionCode(short operand1, short operand2, short result, boolean subtract) {
        CC cc = CC.OKAY;

        System.out.printf("[ALU::getAddSubtractConditionCode] Inputs: op1 = %d, op2 = %d, result = %d, subtract = %b\n",
                operand1, operand2, result, subtract);

        /* Invert operand1 if this is subtraction since detection algorithm works for sum */
        if (subtract) {
            operand1 *= -1;
        }

        /* Do logical XOR of each operand with the result */
        short test1 = (short) (operand1 ^ result);
        short test2 = (short) (operand2 ^ result);

        System.out.printf("[ALU::getAddSubtractConditionCode] XOR results: tes1 = %d, test2 = %d\n",
                test1, test2);

        /* If logical AND of XOR results above is negative, then overflow occurred */
        if ((test1 & test2) < 0) {
            System.out.println("\n[ALU::getAddSubtractConditionCode] Overflow detected!\n");
            cc = CC.OVERFLOW;
        }

        return cc;
    }

    /**
     * Construction for the Arithmetic Logic Unit (ALU) class
     *
     * @param gpr An array of Register objects with the GPRs for the simulation
     * @param mbr Register object with the MBR for the simulation
     */
    ALU(Register[] gpr, Register mbr) {
        /* Allocate storage for GPRs */
        this.gpr = gpr;

        /* Save MBR to local MBR parameter */
        this.mbr = mbr;
    }

    /**
     * This is the main call to process some arithmetic or logic operation
     * @param code Name of the instruction to operate
     * @param r The register of the instruction
     * @param imm The immediate value of the operation
     *
     * @return returns condition code (0-3)
     */
    public CC operate(String code, int r, short imm) {

        return switch (code) {
            case "AMR" -> MemToReg(r, false);
            case "SMR" -> MemToReg(r, true);
            case "AIR" -> ImmToReg(r, false, imm);
            case "SIR" -> ImmToReg(r, true, imm);
            default -> CC.OKAY;
        };
    }

    /**
     * Handles adding/subtracting some value from memory to a register
     * @param r The GPR to perform the addition on
     * @param subtraction Is this operation a subtraction (adding a negative number)
     * @return Returns an int with condition code
     */
    protected CC MemToReg(int r, boolean subtraction){
        CC cc = CC.OKAY;
        short operand1 = (short) mbr.read();
        short operand2 = (short) gpr[r].read();

        System.out.printf("[ALU::MemToReg] Operands are: %d, %d; Subtraction flag is: %b\n",
                operand1, operand2, subtraction);

        if (subtraction) {
            try {
                gpr[r].load((short)(operand2-operand1));

                /* Check result for overflow */
                cc = getAddSubtractConditionCode(operand1,operand2,(short)gpr[r].read(),true);
            } catch (IOException e) {
                System.out.printf("Exception while loading result of subtraction into GPR%d\n",r);
                e.printStackTrace();
            }
        } else {
            try {
                gpr[r].load((short)(operand2+operand1));

                /* Check result for overflow */
                cc = getAddSubtractConditionCode(operand1,operand2,(short)gpr[r].read(),false);
            } catch (IOException e) {
                System.out.printf("Exception while loading result of addition into GPR%d\n",r);
                e.printStackTrace();
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
    protected CC ImmToReg(int r, boolean subtraction, short imm){
        CC cc = CC.OKAY;
        final short x = 31;

        short operand2 = (short) gpr[r].read();

        if(operand2 == 0){
            try {
                if (subtraction) {
                    gpr[r].load((short) -imm);
                } else {
                    gpr[r].load(imm);
                }
                cc = getAddSubtractConditionCode(imm,operand2,(short) gpr[r].read(),subtraction);
            } catch (IOException e){
                System.out.printf("Error while loading result of addition/subtraction to GPR%d", r);
                e.printStackTrace();
            }
        } else {
            try {
                if (subtraction) {
                    gpr[r].load((short)(operand2-imm));
                } else {
                    gpr[r].load((short)(operand2+imm));
                }
                cc = getAddSubtractConditionCode(imm,operand2,(short) gpr[r].read(),subtraction);
            } catch (IOException e){
                System.out.printf("Error while loading result of addition/subtraction to GPR%d", r);
                e.printStackTrace();
            }
        }
        return cc;
    }
}
