/*
 * This file implements the Control Unit (CU) class for the CSCI 6461 project.
 */
package com.csci6461;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Acts as simulated Control Unit (CU) for simple CSCI 6461 simulated computer.
 * It implements the main interface between the GUI and the computer for 
 * setting and getting registers, etc. but will also coordinate all the 
 * simulated function of the computer.
 * 
 * @author imanuelportalatin
 */
public class ControlUnit {
    /**
     * Initialize static configuration variables
     * NOTE: This should probably eventually come from some config file
     * that is loaded at execution
     */
    private static final int MEMORY_SIZE = 2048;     /* Size of main memory */
    private static final long CLOCK_TIMEOUT = 1000;  /* Clock timeout period */
    private static final int NUMBER_OF_GPR = 4;      /* Number of general purpose registers */
    private static final int NUMBER_OF_IXR = 3;      /* Number of general purpose registers */

    /**
     * Parameter to hold the Program Counter (PC) register
     */
    public Register pc;

    /**
     * Parameter to hold the General Purpose Registers (GPR)
     */
    public Register[] gpr;

    /**
     * Parameter to hold the IX Registers (IXR)
     */
    public Register[] ixr;

    /**
     * Parameter to hold the Memory Address Register (MAR)
     */
    public Register mar;

    /**
     * Parameter to hold the Memory Buffer Register (MBR)
     */
    public Register mbr;

    /**
     * Parameter to hold the Instruction Register (IR)
     */
    public Register ir;

    /**
     * Parameter to hold the computer's main memory
     * NOTE: Setting to private for now since I don't think memory needs to 
     *       be read directly outside the CPU but is always loaded to register 
     *       first. We may have to change this as we build out the sim.
     */
    private Memory mainMemory;

    /**
     * Parameter to hold the computer's instruction decoder
     */
    private final InstructionDecoder instructionDecoder;

    /**
     * Parameter to hold the Arithmetic Logic Unit (ALU)
     */
    private final ALU alu;

    public CC controlCode;

    private final int active_cc;

    /**
     * Control Unit constructor will instantiate all registers and load 
     * the ROM program
     */
    public ControlUnit() {
        System.out.println("Initializing control unit...");
        
        /*
         * Create Program Counter (PC) register
         */

        this.pc = new Register("PC",12);

        /*
         * Create appropriate number of General Purpose Registers (GPR)
         */
        this.gpr = new Register[ControlUnit.NUMBER_OF_GPR];
        for (int i = 0; i < ControlUnit.NUMBER_OF_GPR; i++) {
            final String name = String.format("GPR%d", i);
            this.gpr[i] = new Register(name, 16);
        }

        /*
         * Create appropriate number of IX Registers (IXR)
         */
        this.ixr = new Register[ControlUnit.NUMBER_OF_IXR];
        for (int i = 0; i < ControlUnit.NUMBER_OF_IXR; i++) {
            final String name = String.format("IXR%d", i);
            this.ixr[i] = new Register(name, 16);
        }

        /*
         * Create Memory Address Register (MAR)
         */
        this.mar = new Register("MAR",12);

        /*
         * Create Memory Buffer Register (MBR)
         */
        this.mbr = new Register("MBR",16);

        /*
         * Create Instruction Register (IR)
         */
        this.ir = new Register("IR",16);

        /*
         * Create main memory of appropriate size
         */
        try {
            this.mainMemory = new Memory(ControlUnit.MEMORY_SIZE, this.mar, this.mbr);
        } catch(final IOException ioe) {
            System.out.println("Execption while creating computer memory...");
            ioe.printStackTrace();
        }

        /*
         * Create instruction decoder
         */
        this.instructionDecoder = new InstructionDecoder();

        /*
         * Create ALU
         */
        this.alu = new ALU(this.gpr, this.mbr);

        /*
         * Create system clock and initialize to configured timeout
         */
        /*
         * Parameter to hold system clock
         */
        Clock systemClock = new Clock(ControlUnit.CLOCK_TIMEOUT);

        this.controlCode = CC.OKAY;

        this.active_cc = -1;
    }

    /**
     * Method to get a data word from memory; It copied the address to MAR and gets the result from MBR
     *
     * @param address Int with memory address from which to read data
     *
     * @return a short with data read from memory
     */
    public short loadDataFromMemory(final int address) {
        /* Copy the address into the MAR */
        try {
            this.mar.load((short)address);
        } catch(final IOException ioe) {
            System.out.println("Exception while writing to MAR...");
            ioe.printStackTrace();
        }

        /* Signal memory load data into MBR */
        try {
            this.mainMemory.read();
        } catch(final IOException ioe) {
            System.out.println("Exception while writing to memory...");
            ioe.printStackTrace();
        }

        /* Read data from MBR and return */
        return((short) this.mbr.read());
    }

    /**
     * This method writes data to a memory address; It copies the data to MBR and the address to MAR
     * and then calls the method in memory to write the data
     *
     * @param address Int with address in memory in which to load data
     * @param data Short with data to load into memory
     */
    public void writeDataToMemory (final int address, final short data) throws IOException {
        /* Load the address into MAR */
        this.mar.load((short)address);

        /* Load data to MBR */
        this.mbr.load(data);

        /* Call method to load data on MBR into memory */
        this.mainMemory.write();
    }

    /**
     * This method writes data to a memory address; It copies the data to MBR and the address to MAR
     * and then calls the method in memory to write the data,
     *
     */
    public void writeDataToMemory () throws IOException {
        /* Call method to load data on MBR into memory */
        this.mainMemory.write();
    }

    /**
     * Get the first command from memory using the memory class and update the program counter to it.
     */
    public void get_first_command(){
        final short first_code =  (short) mainMemory.get_first_code();
        final boolean[] pc_bits = this.get_bool_array(this.getBinaryString(first_code));
        System.out.println(Arrays.toString(pc_bits));
        this.pc.set_bits(pc_bits);
    }

    /**
     * Method to process a TRAP instruction
     *
     * @param instruction An object implementing the Instruction abstract class for Miscellaneous instructions
     */
    private void processTrap(final Instruction instruction) {
        /* Get trap code from instruction */
        final int[] args = instruction.getArguments();
        System.out.printf("[ControlUnit::processTrap] Arguments for trap code instruction: %d\n", args[0]);

        /* Per instructions, trap logic for Part 1 only fetches memory location 1 and saves it PC  */
        /* NOTE: Memory location 1 should contain the address of memory location 6, which should have a HALT */
        try {
            final short faultAddress = this.loadDataFromMemory(1);

            /* Convert word read from memory to byte array */
            final boolean[] bytes = this.get_bool_array(this.getBinaryString(faultAddress,12));

            /* Load fault address to PC register so we will go to trap routine on next cycle */
            this.pc.load(bytes);

        } catch (final IOException ioe) {
            System.out.println("Exception while reading fault address from memory...");
            ioe.printStackTrace();
        }
    }

    /**
     * Loads a register from memory
     * @param instruction The instruction as is from memory
     * @throws IOException Throws an IO exception
     */
    private void processLD(final Instruction instruction, final boolean index) throws IOException{
        final int[] args;
        args = instruction.getArguments();

        this.getData(args[3],args[1],args[2]);

        final int data = this.mbr.read();

        try {
            if (index){
                this.ixr[args[1]-1].load((short) data);
            } else {
                this.gpr[args[0]].load((short) data);
            }

        } catch (final IOException e) {
            System.out.println("[ERROR]:: Could Not read memory");
            e.printStackTrace();
        }
    }

    /**
     * Stores a register to memory
     * @param instruction The instruction as is from memory
     * @throws IOException Throws an IO exception
     */
    private void processST(final Instruction instruction, final boolean index) throws IOException {
        final int[] args;
        args = instruction.getArguments();

        final short data;
        if(index){
            data = (short) this.ixr[args[1]-1].read();
        } else {
            data = (short) this.gpr[args[0]].read();
        }


        this.writeDataToMemory(this.calculateEA(args[3],args[1],args[2]), data);
    }

    /**
     * Loads an address reference to register
     * @param instruction The instruction as is from memory
     * @throws IOException Throws an IO exception
     */
    private void processLDA(final Instruction instruction) throws  IOException {
        final int[] args;
        args = instruction.getArguments();

        final short effectiveAdr = this.calculateEA(args[3],args[1],args[2]);

        final boolean[] data = this.get_bool_array(this.getBinaryString(effectiveAdr));

        try {
            this.gpr[args[0]].load(data);
        } catch (final IOException e) {
            System.out.println("[ERROR]:: Could Not read memory");
            e.printStackTrace();
        }
    }

    /**
     * Processes addition/subtraction from memory to register (E.g. AMR or MBR)
     *
     * @param instruction Class with decode instruction
     *
     * @return An int with the condition code (0-3)
     */
    private int processMathMR(final Instruction instruction) throws IOException {
        final int[] args;

        /* Get instruction arguments */
        args = instruction.getArguments();

        /* Get data from memory into MBR */
        this.getData(args[3],args[1],args[2]);

        /* Call operate on ALU with Opcode and return condition code */

        return this.alu.operate(instruction.getName(),args[0], (short)args[3]);
    }

    /**
     * Handles the tests to see if a register is zero
     * @param instruction The decoded instruction
     * @param ifZero if we are checking if a val is zero
     * @return Returns if the program counter should be incremented  by 1
     * @throws IOException Throws IO exception
     */
    private boolean processZero(final Instruction instruction, final boolean ifZero) throws IOException {
        final int[] args;
        args = instruction.getArguments(); // Get arguments

        final short effectiveAdr = this.calculateEA(args[3],args[1],args[2]); // convert to effective address
        final int register = args[0];
        final int c = this.gpr[register].read();

        // Run the test to see if the value is equal to zero or not
        if(c == 0 && ifZero) {
            this.pc.load(effectiveAdr);
            return false;
        } else if (c != 0 && !ifZero) {
            this.pc.load(effectiveAdr);
            return false;
        }
        return true;

    }

    /**
     * Process the check for the condition code
     * @param instruction the decoded instruction
     * @return returns if the program counter should be updated
     * @throws IOException throws IO exception
     */
    private boolean processjumpCC(final Instruction instruction) throws IOException {
        final int[] args;
        args = instruction.getArguments(); // Get arguments
        final short effectiveAdr = this.calculateEA(args[3],args[1],args[2]); // convert to effective address

        final int cc = args[0];

        if (cc == active_cc){
            this.pc.load(effectiveAdr);
            return false;
        }
        return true;
    }

    /**
     * Processes an unconditional jump to an address
     * @param instruction the decoded instruction
     * @return Returns that the program counter should not be updated
     * @throws IOException Throws an IO exception
     */
    private boolean processJMA(final Instruction instruction) throws IOException {
        final int[] args;
        args = instruction.getArguments(); // Get arguments
        final short effectiveAdr = this.calculateEA(args[3],args[1],args[2]); // convert to effective address

        this.pc.load(effectiveAdr);
        return false;
    }

    /**
     * Jump if the register is >= 0;
     * @param instruction The decoded instruction
     * @return Returns if the program counter should be incremented by 1
     * @throws IOException Throws IO exception
     */
    private boolean processJGE(final Instruction instruction) throws IOException {
        final int[] args;
        args = instruction.getArguments(); // Get arguments

        final short effectiveAdr = this.calculateEA(args[3],args[1],args[2]); // convert to effective address
        final int register = args[0];
        final int c = this.gpr[register].read();

        // Run the test to see if the value is equal to zero or not
        if(c >= 0) {
            this.pc.load(effectiveAdr);
            return false;
        }

        return true;
    }

    private boolean processSOB(final Instruction instruction) throws IOException {
        final int[] args;
        args = instruction.getArguments(); // Get arguments

        final short effectiveAdr = this.calculateEA(args[3],args[1],args[2]); // convert to effective address
        final int register = args[0];
        int c = this.gpr[register].read();
        c--; // Subtract 1 from c

        // load c back into the register
        this.gpr[register].load((short)c);

        if(c >0) {
            this.pc.load(effectiveAdr);
            return false;
        }

        return true;
    }

    /**
     * Method to execute single step by getting the next instruction in
     * memory, decoding it and executing it
     *
     * @return A boolean set to false whenever halt is received
     */
    public boolean singleStep() throws IOException {
        /* Get next instruction address from PC and convert to int */
        final int iPC = this.pc.read();
        System.out.printf("[ControlUnit::singleStep] Next instruction address is %d\n", iPC);

        /* Get instruction at address indicated by PC */
        final short instruction = this.loadDataFromMemory(iPC);
        System.out.printf("[ControlUnit::singleStep] Have next instruction: %s\n",
                this.getBinaryString(instruction));

        /* Load the current instruction into the IR */
        this.ir.load(this.get_bool_array(Integer.toBinaryString(instruction)));

        /* Decode the instruction */
        final Instruction decodedInstruction = this.instructionDecoder.decode(instruction);

        /* If decoder return null, something went wrong */
        if (decodedInstruction == null) {
            /* Invalid Instruction; throw exception... */
            final String error = String.format("Opcode for instruction %s is invalid!",
                    this.getBinaryString(instruction));
            throw new IOException(error);
        }

        /* Process instruction according to translated Opcode */
        System.out.printf("[ControlUnit::singleStep] Processing instruction: %s\n", decodedInstruction.getName());

        final String name = decodedInstruction.getName();

        /* Check to see if the code is a "special" instruction */
        if(Objects.equals(name, "HLT")) {
            System.out.println("[ControlUnit::singleStep] Processing Halt instruction...\n");
            return(false);
        } else if(Objects.equals(name, "TRAP")) {
            System.out.println("[ControlUnit::singleStep] Processing Trap instruction...\n");
            this.processTrap(decodedInstruction);
            return(true);
        }

        boolean increment_pc = true;

        switch (name) {
            case "LDR" -> {
                System.out.println("[ControlUnit::singleStep] Processing LDR instruction...\n");
                this.processLD(decodedInstruction, false);
            }
            case "STR" -> {
                System.out.println("[ControlUnit::singleStep] Processing STR instruction...\n");
                this.processST(decodedInstruction, false);
            }
            case "LDA" -> {
                System.out.println("[ControlUnit::singleStep] Processing LDA instruction...\n");
                this.processLDA(decodedInstruction);
            }
            case "LDX" -> {
                System.out.println("[ControlUnit::singleStep] Processing LDX instruction...\n");
                this.processLD(decodedInstruction, true);
            }
            case "STX" -> {
                System.out.println("[ControlUnit::singleStep] Processing STX instruction...\n");
                this.processST(decodedInstruction, true);
            }
            case "AMR" -> {
                System.out.println("[ControlUnit::singleStep] Processing AMR instruction...\n");
                this.processMathMR(decodedInstruction);
            }
            case "SMR" -> {
                System.out.println("[ControlUnit::singleStep] Processing SMR instruction...\n");
                this.processMathMR(decodedInstruction);
            }
            case "JZ" -> {
                System.out.println("[ControlUnit::singleStep] Processing JZ instruction...\n");
                increment_pc = processZero(decodedInstruction, true);
            }
            case "JNE" -> {
                System.out.println("[ControlUnit::singleStep] Processing JNE instruction...\n");
                increment_pc = processZero(decodedInstruction, false);
            }
            case "JCC" -> {
                System.out.println("[ControlUnit::singleStep] Processing JCC instruction...\n");
                increment_pc = processjumpCC(decodedInstruction);
            }
            case "JMA" -> {
                System.out.println("[ControlUnit::singleStep] Processing JMA instruction...\n");
                increment_pc = processJMA(decodedInstruction);
            }
            case "JGE" -> {
                System.out.println("[ControlUnit::singleStep] Processing JGE instruction...\n");
                increment_pc = processJGE(decodedInstruction);
            }
        }

        if (increment_pc)
        {
            short count = (short) this.pc.read();
            count++;
            final boolean[] _new_count = this.get_bool_array(this.getBinaryString(count));
            this.pc.set_bits(_new_count);
        }

        return(true);
    }

    /**
     * Calculate the effective address for the memory
     * @param address The address given in the code
     * @param ix the index register
     * @param i if the reference is indirect
     * @return returns the new address
     */
    private short calculateEA(final int address, int ix, final int i) throws IOException {
        final short ea;

        System.out.printf("[ControlUnit::CalculateEA] Fields are: Address = %d, IX = %d, I = %d\n",
                address, ix, i);
        /* If I field = 0; then NO indirect addressing */
        if (i == 0) {
            /* If IX = 0; then NO indirect addressing and EA = address */
            if (ix == 0) {
                System.out.println("[ControlUnit::CalculateEA] Direct address without indexing.");
                ea = (short)address;
            } else {
                /* If IX > 0; then we're using indexing */
                /* NOTE: We must adjust for Java 0 index since IX registers start at 1 NOT 0 */
                if (ix <= ControlUnit.NUMBER_OF_IXR) {
                    /* Effective address is address field + contents of index field indexed by IX: */
                    /*                           EA = address + c(IX)                               */
                    ix--;   /* Decrement IX to adjust for Java array indexing */
                    System.out.println("[ControlUnit::CalculateEA] Direct address with indexing.");
                    ea = (short) (address + this.ixr[ix].read());
                } else {
                    final String error = String.format("Error: Index register out of bounds: %d\n", ix);
                    throw new IOException(error);
                }
            }
        }
        /* I = 1; Use indirect addressing */
        else {
            /* If IX = 0; then indirect address but NO indexing */
            if (ix == 0) {
                System.out.println("[ControlUnit::CalculateEA] Indirect address without indexing");
                ea = this.loadDataFromMemory(address);
            } else {
                /* If IX > 0; then we're using indexing */
                /* NOTE: We must adjust for Java 0 index since IX registers start at 1 NOT 0 */
                if (ix <= ControlUnit.NUMBER_OF_IXR) {
                    /* Effective address is contents of memory at location indicated by address field   */
                    /* + contents of index field indexed by IX:                                         */
                    /*                           EA = c(address) + c(IX)                                */
                    ix--;   /* Decrement IX to adjust for Java array indexing */
                    System.out.println("[ControlUnit::CalculateEA] Direct address with indexing.");

                    /* Place address in MAR and call method to get indirect address into MBR */

                    ea = (short) (this.loadDataFromMemory(address) + this.ixr[ix].read());
                } else {
                    final String error = String.format("Error: Index register out of bounds: %d\n", ix);
                    throw new IOException(error);
                }
            }
        }
        System.out.printf("[ControlUnit::CalculateEA] Effective address is: %s\n",ea);
        return ea;
    }

    /**
     * Prints the main memory to the console
     */
    public void printMem(){
        this.mainMemory.printMemory();
    }

    /**
     * Get the 16-bit binary string
     * @param word 16-bit word to convert to binary
     * @return Returns the binary string with all 16-bits
     */
    private String getBinaryString(final short word){
        String val =  String.format("%16s", Integer.toBinaryString(word)).replace(' ', '0');
        if(val.length() > 16){
            val = val.substring(val.length()-16);
        }

        return val;
    }

    /**
     * Get the n-bit binary string
     * @param word 16-bit word to convert to binary
     * @param n The cut-off point for the string
     * @return Returns the binary string with all 16-bits
     */
    private String getBinaryString(final short word, final int n){
        String val =  String.format("%16s", Integer.toBinaryString(word)).replace(' ', '0');
        if(n <= 16){
            val = val.substring(val.length()-n);
        } else {
            val = val.substring(val.length()-16);
        }

        return val;
    }

    /**
     * Gets data from main memory into MBR
     * @param address The physical address given in opcode
     * @param ix The index register
     * @param i The indirect addressing state
     */
    private void getData(final int address, final int ix, final int i) throws IOException{
        /* Calculate effective address with indexing and indirection (if any) */
        final short effectiveAddress = this.calculateEA(address, ix, i);

        /* Save effective address into MAR */
        this.mar.load(effectiveAddress);

        /* Call method to transfer memory address to MBR */
        this.mainMemory.read();
    }

    /**
     * Converts a binary string to a boolean array
     * @param binaryString The binary string to convert
     * @return the boolean array.
     */
    private boolean[] get_bool_array(final String binaryString) {

        final char[] binary = binaryString.toCharArray(); // Convert to character array
        final boolean[] data = new boolean[binary.length]; // Create a new boolean array

        // Loop through array and flip bits where a 1 is present
        for(int x=0; x<binary.length;x++){
            if(binary[x] == '1'){
                data[x] = true;
            }
        }

        return data;
    }

    /**
     * Performs the read memory action
     */
    public void read_mem() {
        try {
            this.mainMemory.read();
        } catch (final IOException e) {
            System.out.println("[ERROR]:: Could not read memory");
            e.printStackTrace();
        }
    }
    
}
