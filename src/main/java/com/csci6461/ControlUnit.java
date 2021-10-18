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
     * Parameter to hold condition code
     */
    CC cc;

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

//    /**
//     * Parameter to hold the computer's main memory
//     * NOTE: Setting to private for now since I don't think memory needs to
//     *       be read directly outside the CPU but is always loaded to register
//     *       first. We may have to change this as we build out the sim.
//     */
//    private Memory mainMemory;

    /**
     * Parameter to hold Cache for the computer, which also acts as the interface to Main Memory
     */
    private Cache mainMemory;

    /**
     * Parameter to hold the computer's instruction decoder
     */
    private final InstructionDecoder instructionDecoder;

    /**
     * Parameter to hold the Arithmetic Logic Unit (ALU)
     */
    private final ALU alu;

    public CC controlCode;

    /**
     * Parameter to hold system clock
     */
    private final Clock systemClock;

    /**
     * Control Unit constructor will instantiate all registers and load 
     * the ROM program
     */
    public ControlUnit() {
        System.out.println("Initializing control unit...");

        /*
         * Initialize condition code to OKAY
         */
        cc = CC.OKAY;
        
        /*
         * Create Program Counter (PC) register
         */

        pc = new Register("PC",12);

        /*
         * Create appropriate number of General Purpose Registers (GPR)
         */
        gpr = new Register[NUMBER_OF_GPR];
        for (int i = 0; i < NUMBER_OF_GPR; i++) {
            String name = String.format("GPR%d", i);
            gpr[i] = new Register(name, 16);
        }

        /*
         * Create appropriate number of IX Registers (IXR)
         */
        ixr = new Register[NUMBER_OF_IXR];
        for (int i = 0; i < NUMBER_OF_IXR; i++) {
            String name = String.format("IXR%d", i);
            ixr[i] = new Register(name, 16);
        }

        /*
         * Create Memory Address Register (MAR)
         */
        mar = new Register("MAR",12);

        /*
         * Create Memory Buffer Register (MBR)
         */
        mbr = new Register("MBR",16);

        /*
         * Create Instruction Register (IR)
         */
        ir = new Register("IR",16);

        /*
         * Create main memory of appropriate size
         */
        try {
            mainMemory = new Cache(MEMORY_SIZE,mar,mbr);
        } catch(IOException ioe) {
            System.out.println("Execption while creating computer memory...");
            ioe.printStackTrace();
        }

        /*
         * Create instruction decoder
         */
        instructionDecoder = new InstructionDecoder();

        /*
         * Create ALU
         */
        alu = new ALU(gpr,mbr);

        /*
         * Create system clock and initialize to configured timeout
         */
        systemClock = new Clock(CLOCK_TIMEOUT);

        controlCode = CC.OKAY;
    }

    /**
     * Method to get a data word from memory; It copied the address to MAR and gets the result from MBR
     *
     * @param address Int with memory address from which to read data
     *
     * @return a short with data read from memory
     */
    public short loadDataFromMemory(int address) {
        /* Copy the address into the MAR */
        try {
            mar.load((short)address);
        } catch(IOException ioe) {
            System.out.println("Exception while writing to MAR...");
            ioe.printStackTrace();
        }

        /* Signal memory load data into MBR */
        try {
            mainMemory.read();
        } catch(IOException ioe) {
            System.out.println("Exception while writing to memory...");
            ioe.printStackTrace();
        }

        /* Read data from MBR and return */
        return((short) mbr.read());
    }

    /**
     * This method writes data to a memory address; It copies the data to MBR and the address to MAR
     * and then calls the method in memory to write the data
     *
     * @param address Int with address in memory in which to load data
     * @param data Short with data to load into memory
     */
    public void writeDataToMemory (int address, short data) throws IOException {
        /* Load the address into MAR */
        mar.load((short)address);

        /* Load data to MBR */
        mbr.load(data);

        /* Call method to load data on MBR into memory */
        mainMemory.write();
    }

    /**
     * This method writes data to a memory address; It copies the data to MBR and the address to MAR
     * and then calls the method in memory to write the data,
     *
     */
    public void writeDataToMemory () throws IOException {
        /* Call method to load data on MBR into memory */
        mainMemory.write();
    }

    /**
     * Get the first command from memory using the memory class and update the program counter to it.
     */
    public void get_first_command(){
        short first_code =  (short) this.mainMemory.get_first_code();
        boolean[] pc_bits = get_bool_array(getBinaryString(first_code));
        System.out.println(Arrays.toString(pc_bits));
        pc.set_bits(pc_bits);
    }

    /**
     * Method to process a TRAP instruction
     *
     * @param instruction An object implementing the Instruction abstract class for Miscellaneous instructions
     */
    private void processTrap(Instruction instruction) {
        /* Get trap code from instruction */
        int[] args = instruction.getArguments();
        System.out.printf("[ControlUnit::processTrap] Arguments for trap code instruction: %d\n", args[0]);

        /* Per instructions, trap logic for Part 1 only fetches memory location 1 and saves it PC  */
        /* NOTE: Memory location 1 should contain the address of memory location 6, which should have a HALT */
        try {
            short faultAddress = loadDataFromMemory(1);

            /* Convert word read from memory to byte array */
            boolean[] bytes = get_bool_array(getBinaryString(faultAddress,12));

            /* Load fault address to PC register so we will go to trap routine on next cycle */
            pc.load(bytes);

        } catch (IOException ioe) {
            System.out.println("Exception while reading fault address from memory...");
            ioe.printStackTrace();
        }
    }

    /**
     * Loads a register from memory
     * @param instruction The instruction as is from memory
     * @throws IOException Throws an IO exception
     */
    private void processLD(Instruction instruction, boolean index) throws IOException{
        int[] args;
        args = instruction.getArguments();

        getData(args[3],args[1],args[2]);

        int data = mbr.read();

        try {
            if (index){
                ixr[args[1]-1].load((short) data);
            } else {
                gpr[args[0]].load((short) data);
            }

        } catch (IOException e) {
            System.out.println("[ERROR]:: Could Not read memory");
            e.printStackTrace();
        }
    }

    /**
     * Stores a register to memory
     * @param instruction The instruction as is from memory
     * @throws IOException Throws an IO exception
     */
    private void processST(Instruction instruction, boolean index) throws IOException {
        int[] args;
        args = instruction.getArguments();

        short data;
        if(index){
            data = (short) ixr[args[1]-1].read();
        } else {
            data = (short) gpr[args[0]].read();
        }


        writeDataToMemory(calculateEA(args[3],args[1],args[2]), data);
    }

    /**
     * Loads an address reference to register
     * @param instruction The instruction as is from memory
     * @throws IOException Throws an IO exception
     */
    private void processLDA(Instruction instruction) throws  IOException {
        int[] args;
        args = instruction.getArguments();

        short effectiveAdr = calculateEA(args[3],args[1],args[2]);

        boolean[] data = get_bool_array(getBinaryString(effectiveAdr));

        try {
            gpr[args[0]].load(data);
        } catch (IOException e) {
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
    private void processMathMR(Instruction instruction) throws IOException {
        int[] args;

        /* Get instruction arguments */
        args = instruction.getArguments();

        /* Get data from memory into MBR */
        getData(args[3],args[1],args[2]);

        /* Call operate on ALU with Opcode and return condition code */

        cc = alu.operate(instruction.getName(),args[0], (short)args[3]);
    }
    
//    /**
//     * Method to trigger program execution
//     *
//     * @throws InterruptedException When current thread is interrupted by another thread
//     */
//    public void run() throws InterruptedException {
//        /*
//         * Parameter to start/stop program execution
//         */
//        boolean aContinue = true;
//        while (aContinue) {
//
//            systemClock.waitForNextTick();
//
//            try {
//                aContinue = singleStep();
//            } catch (IOException ioe) {
//                System.out.println("Error during step execution");
//                ioe.printStackTrace();
//            }
//        }
//    }

    /**
     * Method to execute single step by getting the next instruction in
     * memory, decoding it and executing it
     *
     * @return A boolean set to false whenever halt is received
     */
    public boolean singleStep() throws IOException {
        /* Get next instruction address from PC and convert to int */
        int iPC = pc.read();
        System.out.printf("[ControlUnit::singleStep] Next instruction address is %d\n", iPC);

        /* Get instruction at address indicated by PC */
        short instruction = loadDataFromMemory(iPC);
        System.out.printf("[ControlUnit::singleStep] Have next instruction: %s\n",
                getBinaryString(instruction));

        /* Load the current instruction into the IR */
        ir.load(get_bool_array(Integer.toBinaryString((int)instruction)));

        /* Decode the instruction */
        Instruction decodedInstruction = instructionDecoder.decode(instruction);

        /* If decoder return null, something went wrong */
        if (decodedInstruction == null) {
            /* Invalid Instruction; throw exception... */
            String error = String.format("Opcode for instruction %s is invalid!",
                    getBinaryString(instruction));
            throw new IOException(error);
        }

        /* Process instruction according to translated Opcode */
        System.out.printf("[ControlUnit::singleStep] Processing instruction: %s\n", decodedInstruction.getName());

        String name = decodedInstruction.getName();

        /* Check to see if the code is a "special" instruction */
        if(Objects.equals(name, "HLT")) {
            System.out.println("[ControlUnit::singleStep] Processing Halt instruction...\n");
            return(false);
        } else if(Objects.equals(name, "TRAP")) {
            System.out.println("[ControlUnit::singleStep] Processing Trap instruction...\n");
            processTrap(decodedInstruction);
            return(true);
        }

        switch (name) {
            case "LDR" -> {
                System.out.println("[ControlUnit::singleStep] Processing LDR instruction...\n");
                processLD(decodedInstruction, false);
            }
            case "STR" -> {
                System.out.println("[ControlUnit::singleStep] Processing STR instruction...\n");
                processST(decodedInstruction, false);
            }
            case "LDA" -> {
                System.out.println("[ControlUnit::singleStep] Processing LDA instruction...\n");
                processLDA(decodedInstruction);
            }
            case "LDX" -> {
                System.out.println("[ControlUnit::singleStep] Processing LDX instruction...\n");
                processLD(decodedInstruction, true);
            }
            case "STX" -> {
                System.out.println("[ControlUnit::singleStep] Processing STX instruction...\n");
                processST(decodedInstruction, true);
            }
            case "AMR" -> {
                System.out.println("[ControlUnit::singleStep] Processing AMR instruction...\n");
                processMathMR(decodedInstruction);
            }
            case "SMR" -> {
                System.out.println("[ControlUnit::singleStep] Processing SMR instruction...\n");
                processMathMR(decodedInstruction);
            }
        }

        short count = (short)pc.read();
        count++;
        boolean[] _new_count = get_bool_array(getBinaryString(count));
        pc.set_bits(_new_count);

        return(true);
    }

    /**
     * Calculate the effective address for the memory
     * @param address The address given in the code
     * @param ix the index register
     * @param i if the reference is indirect
     * @return returns the new address
     */
    private short calculateEA(int address, int ix, int i) throws IOException {
        short ea;

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
                if (ix <= NUMBER_OF_IXR) {
                    /* Effective address is address field + contents of index field indexed by IX: */
                    /*                           EA = address + c(IX)                               */
                    ix--;   /* Decrement IX to adjust for Java array indexing */
                    System.out.println("[ControlUnit::CalculateEA] Direct address with indexing.");
                    ea = (short) (address + ixr[ix].read());
                } else {
                    String error = String.format("Error: Index register out of bounds: %d\n", ix);
                    throw new IOException(error);
                }
            }
        }
        /* I = 1; Use indirect addressing */
        else {
            /* If IX = 0; then indirect address but NO indexing */
            if (ix == 0) {
                System.out.println("[ControlUnit::CalculateEA] Indirect address without indexing");
                ea = loadDataFromMemory(address);
            } else {
                /* If IX > 0; then we're using indexing */
                /* NOTE: We must adjust for Java 0 index since IX registers start at 1 NOT 0 */
                if (ix <= NUMBER_OF_IXR) {
                    /* Effective address is contents of memory at location indicated by address field   */
                    /* + contents of index field indexed by IX:                                         */
                    /*                           EA = c(address) + c(IX)                                */
                    ix--;   /* Decrement IX to adjust for Java array indexing */
                    System.out.println("[ControlUnit::CalculateEA] Direct address with indexing.");

                    /* Place address in MAR and call method to get indirect address into MBR */

                    ea = (short) (loadDataFromMemory(address) + ixr[ix].read());
                } else {
                    String error = String.format("Error: Index register out of bounds: %d\n", ix);
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
        mainMemory.printMemory();
    }

    /**
     * Prints a line in the cache
     *
     * @param n Integer number of line to print
     */
    public void printCacheLine(Short n) { mainMemory.printCacheLine(n); }

    /**
     * Get the 16-bit binary string
     * @param word 16-bit word to convert to binary
     * @return Returns the binary string with all 16-bits
     */
    private String getBinaryString(short word){
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
    private String getBinaryString(short word, int n){
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
    private void getData(int address, int ix, int i) throws IOException{
        /* Calculate effective address with indexing and indirection (if any) */
        short effectiveAddress = calculateEA(address, ix, i);

        /* Save effective address into MAR */
        mar.load(effectiveAddress);

        /* Call method to transfer memory address to MBR */
        mainMemory.read();
    }

    /**
     * Converts a binary string to a boolean array
     * @param binaryString The binary string to convert
     * @return the boolean array.
     */
    private boolean[] get_bool_array(String binaryString) {

        char[] binary = binaryString.toCharArray(); // Convert to character array
        boolean[] data = new boolean[binary.length]; // Create a new boolean array

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
            mainMemory.read();
        } catch (IOException e) {
            System.out.println("[ERROR]:: Could not read memory");
            e.printStackTrace();
        }
    }
    
}
