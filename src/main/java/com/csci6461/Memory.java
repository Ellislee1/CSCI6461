/**
 * This file implements the main memory for the CSCI 6461 simple computer
 * simulation.
 */
package com.csci6461;

import java.io.IOException;

/**
 * The Memory class is a container for an array of 16-bit words (E.g. shorts) 
 * of size equal to the required memory size (2048 or 4096 words according to 
 * the CSCI 6461 project spec. It also implements methods to simulate the various
 * memory functions such as read and write and a simple addressing scheme where 
 * the address corresponds to the slot in the data array of a single word.
 * 
 * @author imanuelportalatin
 */
public class Memory {
   /**
    * Member variable to hold data array, which will be initialized by the 
    * constructor
    */ 
    protected final short[] data;
    /**
     * Total size of the memory array in 16-bit words
     */
    private final int size;
    /**
     * Parameter to hold Memory Address Register (MAR) to hold address for read/write
     */
    Register mar;
    /**
     * Parameter to hold Memory Buffer Register (MBR) to receive input/output data
     */
    Register mbr;
    /**
     * The memory class constructor creates a new data storage array of 
     * the specified size and assigns it to the data private variable. The 
     * size if also saved for reference.
     * 
     * @param s Size of memory array in number of 16-bit words
     * @param mar Register object to use as Memory Address Register (MAR)
     * @param mbr Register object to use as Memory Buffer Register (MBR)
     * 
     * @throws IOException If an invalid memory size is specified
     */
    public Memory(int s, Register mar, Register mbr) throws IOException {
        if (s <= 0) {
            String error = String.format(
               "Invalid memory size: %d; Memory size must greater than zero.",
                s);
            throw new IOException(error);
        }
        
        /* Save size and initalize data to new array of specified size */
        size = s;
        data = new short[size];

        /* Set MAR and MBR parameters */
        this.mar = mar;
        this.mbr = mbr;
    }

    /**
     * This method writes the data stored in MBR into the address indicated by MAR
     *
     * @throws IOException If address in MAR is invalid
     */
    public void write() throws IOException {
        /* Get the address from the MAR */
        short address = (short) mar.read();

        /* Check for out of bounds address */
        if (address < 0 || address >= size) {
            String error = String.format(
                    "Invalid memory address: %d; Addresses must be between 0 and %d.",
                    address, size);
            throw new IOException(error);
        }

        /* Copy value in MBR into the specified address in memory */
        data[address] = (short) mbr.read();
    }

    /**
     * This method reads data at location specified by MAR and copies it to MBR
     *
     * @throws IOException If an invalid address is received
     */
    public void read() throws IOException {
        /* Get the address from MAR */
        int address = mar.read();

        if (address < 0 || address >= size) {
            String error = String.format(
                    "Invalid memory address: %d; Addresses must be between 0 and %d.",
                    address, size);
            throw new IOException(error);
        }
        /* Return data at specified location */
        short word = data[address];
        System.out.printf("Read data word from memory: %s\n",
                Integer.toBinaryString(0xffff & word));

        /* Copy retrieved word into MBR */
        mbr.load(word);
    }

    /**
     * A method for printing the populated memory dump to the console
     */
    public void printMemory() {
        // |-------------|-------------|
        // |   ADDRESS   |    VALUE    |
        // |-------------|-------------|
        System.out.println("\n\n         MEMORY DUMP         ");
        System.out.println("|-------------|-------------|");
        System.out.println("|   ADDRESS   |    VALUE    |");
        System.out.println("|-------------|-------------|");

        for (int i = 0; i< data.length; i++){
            if (data[i]!=0){
                String pos = "0x"+ formatHex(Integer.toHexString(i & 0xffff)).toUpperCase();
                String val = "0x"+ formatHex(Integer.toHexString(data[i] & 0xffff)).toUpperCase();
                System.out.println("|   "+pos+"    |   "+val+"    |");
            }
        }
        System.out.println("|-------------|-------------|");

    }

    /**
     * Get the data
     * @return Returns the memory data Array
     */
    public short[] getData(){
        return this.data;
    }

    /**
     * Formats the string to look like a standard 2 byte hex
     * @param s The unformatted string
     * @return Returns the formatted string
     */
    private String formatHex(String s) {
        String newS = s;
        if (s.length() < 4) {
            for (int i = 0; i < 4 - s.length(); i++){
                newS = "0"+newS;
            }
        }
        return  newS;
    }

    /**
     * Gets the first address past the reserved addresses that contains a code
     * @return Returns an address location
     */
    public int get_first_code() {
        // Loop through memory array and look for the first non-empty element after 5
        for(int i = 6; i< size; i++){
            if(this.data[i] != 0){
                return i; // return first instruction
            }
        }
        // If no instruction is found return the halt code
        return 5;
    }

}
