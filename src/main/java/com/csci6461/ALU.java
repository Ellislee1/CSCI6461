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
    public CC operate(String code, int r, short imm) throws IOException {

        return switch (code) {
            case "AMR" -> MemToReg(r, false);
            case "SMR" -> MemToReg(r, true);
            case "AIR" -> ImmToReg(r, false, imm);
            case "SIR" -> ImmToReg(r, true, imm);
            case "MLT" -> RegToReg("MLT", r, imm);
            case "DVD" -> RegToReg("DVD", r, imm);
            case "TRR" -> RegToReg("TRR", r, imm);
            case "AND" -> RegToReg("AND", r, imm);
            case "ORR" -> RegToReg("ORR", r, imm);
            case "NOT" -> RegToReg("NOT", r, imm);

            default -> CC.OKAY;
        };
    }

    /**
     * This method implements register to register math and logic operations
     *
     * @param code String containing the three-letter code of the operation
     * @param rx Int containing the number of the register containing the first operand
     * @param ry Short containing the number of the register containing the second operand
     *
     * @return The condition code resulting from the operation: OVERFLOW or DIVZERO if overflow
     *         or divide by zero, or EQUALORNOT if operands are equal
     * @throws IOException When a register load throws and exception
     */
    protected CC RegToReg(String code, int rx, short ry) throws IOException {
        CC cc = CC.OKAY;

        System.out.printf("[ALU::RegToReg] Processing logic operation %s with rx = %d, ry = %d\n",
                code, rx, ry);

        /* Make sure rx and ry are valid for divide and multiply */
        if ((code == "DVD") || (code == "MLT")) {
            if ((rx != 0 && rx != 2) || (ry != 0 && ry != 2)) {
                String error = String.format("Invalid register for multiply or divide.");
                throw (new IOException(error));
            }
        }

        short operand1 = (short) gpr[rx].read();
        short operand2 = (short) gpr[ry].read();

        System.out.printf("[ALU::RegToReg] Operands are: %d, %d;  code is: %s\n", operand1,operand2, code);

        switch (code) {
            case "DVD" -> {
                System.out.println("[ALU::RegToReg] Performing division...");
                /* Check for divide by zero */
                if (operand2 == 0) {
                    cc = CC.DIVZERO;
                    break;
                }
                short quotient = (short) (operand1 / operand2);
                short remainder = (short) (operand1 % operand2);
                System.out.printf("[ALU::RegToReg] Completed division: quotient = %d, remainder = %d\n",
                        quotient, remainder);
                /* Save quotient to rx and remainder to rx + 1 */
                gpr[rx].load(quotient);
                gpr[rx+1].load(remainder);
            }
            case "TRR" -> {
                System.out.println("[ALU::RegToReg] Testing for equality...");
                if (operand1 == operand2) {
                    System.out.println("[ALU::RegToReg] Operands are equal!");
                    cc = CC.EQUALORNOT;
                } else {
                    System.out.println("[ALU::RegToReg] Operands are NOT equal!");
                }
                break;
             }
            case "AND" -> {
                System.out.println("[ALU::RegToReg] Performing AND operation...");
                short result = (short)(operand1 & operand2);
                System.out.printf("[ALU::RegToReg] Result of %s & %s is %s\n",
                        Integer.toBinaryString((int)operand1), Integer.toBinaryString((int)operand2),
                        Integer.toBinaryString((int)result));
                /* Save output to rx */
                gpr[rx].load(result);
                break;
            }
            case "ORR" -> {
                System.out.println("[ALU::RegToReg] Performing OR operation...");
                short result = (short)(operand1 | operand2);
                System.out.printf("[ALU::RegToReg] Result of %s | %s is %s\n",
                        Integer.toBinaryString((int)operand1), Integer.toBinaryString((int)operand2),
                        Integer.toBinaryString((int)result));
                /* Save output to rx */
                gpr[rx].load(result);
                break;
            }
            case "NOT" -> {
                System.out.println("[ALU::RegToReg] Performing NOT operation...");
                short result = (short)(~operand1);
                System.out.printf("[ALU::RegToReg] Result of !%s is %s\n",
                        Integer.toBinaryString((int)operand1), Integer.toBinaryString((int)result));
                /* Save output to rx */
                gpr[rx].load(result);
                break;
            }
        }

        return cc;
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
