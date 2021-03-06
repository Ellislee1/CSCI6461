/*
 * This file implements the Decoder Config class for the CSCI 6461 project.
 */
package com.csci6461;

import java.util.*;

/**
 * This class defines the configuration for the instruction decoder of the
 * CSCI 6461 computer simulation. It loads basic information about instructions
 * like the Opcode and the class used to implement it.
 *
 * @author imanuelportalatin
 */
public class DecoderConfig {
    /**
     * This map enables us to look up instructions by their Opcode
     */
    private final Map<Integer, Instruction> instructions = new HashMap<>();

    /**
     * Decoder config constructor that initializes the map of valid instructions.
     * When implementing a new instruction, add a call to the 'instruction' map's put method and
     * pass the Opcode as the key and new object of a class implementing the Instruction abstract
     * class and pass the instruction's name (I.e. HLT, LDR, ADD, etc.) to the constructor.
     * The class implementing the abstract Instruction class must implement a method to decode
     * and return parameters for that type of instruction.
     */
    DecoderConfig() {
        instructions.put(000, new MiscInstruction("HLT"));
        instructions.put(036, new MiscInstruction("TRAP"));
        instructions.put(001, new MemOp("LDR"));
        instructions.put(002, new MemOp("STR"));
        instructions.put(003, new MemOp("LDA"));
        instructions.put(041, new MemOp("LDX"));
        instructions.put(042, new MemOp("STX"));
        instructions.put(004, new MathMR("AMR"));
        instructions.put(005, new MathMR("SMR"));
        instructions.put(010, new MemOp("JZ"));
        instructions.put(011, new MemOp("JNE"));
        instructions.put(012, new MemOp("JCC"));
        instructions.put(013, new MemOp("JMA"));
        instructions.put(016, new MemOp("SOB"));
        instructions.put(014, new MemOp("JSR"));
        instructions.put(015, new MemOp("RFS"));
        instructions.put(017, new MemOp("JGE"));
        instructions.put(020, new MathRR("MLT"));
        instructions.put(021, new MathRR("DVD"));
        instructions.put(022, new MathRR("TRR"));
        instructions.put(023, new MathRR("AND"));
        instructions.put(024, new MathRR("ORR"));
        instructions.put(025, new MathRR("NOT"));
        instructions.put(061, new IoOp("IN"));
        instructions.put(062, new IoOp("OUT"));
        instructions.put(006,new ImMath("AIR"));
        instructions.put(007,new ImMath("SIR"));
        instructions.put(031, new BitWiseOperator("SRC"));
        instructions.put(032, new BitWiseOperator("RRC"));
    }

    /**
     * Method to get an instruction from its Opcode
     *
     * @param opCode Instruction object implementing instruction of this type
     *
     * @return Instruction object or null if instruction is not configured
     */
    public Instruction getInstruction(int opCode){

        return instructions.get(opCode);
    }

}
