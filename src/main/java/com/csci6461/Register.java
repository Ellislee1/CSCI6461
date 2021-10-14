/*
 * This file implements the register class for the CSCI 6461 project
 */
package com.csci6461;

/*
 * Import IOException, Arrays and BitSet class
 */
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author imanuelportalatin
 */
public class Register extends CBitSet {
    /**
     * Name of the register being implemented
     */
    private final String name;
    
    /**
     * Default constructor initializes name and size
     * 
     * @param n Name of the register being created
     * @param s Register size in bits
     */
    public Register(String n, int s) {
        super(s);
        
        System.out.printf("Creating register %s with size %s.\n", n, s);
        name = n;
    }
    
    /**
     * Method to get this register's name property
     * 
     * @return this register's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Method to get this register's size property
     * 
     * @return this register's size in bits
     */
    public int getSize() {
        return this.get_size();
    }

    /**
     * Load data into the bit set using boolean arrays
     * @param data A boolean array
     * @throws IOException Throws an IO Exception is item can not load
     */
    public void load(boolean[] data) throws IOException{
        System.out.printf("[Register::load] Input data for register %s: %s\n",
                name, Arrays.toString(data));

        this.set_bits(data);
    }

    /**
     * Method to load a data word as a short
     *
     * @param data Short containing data word to load
     * @throws IOException Throws a IO exception if item can not be loaded
     */
    public void load(short data) throws IOException {
        load(get_bool_array(Integer.toBinaryString(0xffff & data)));
    }

    /**
     * This methods gets an array of ints with the position of the
     * bits currently set in the register
     * NOTE: Unlike the load method above, if there's overflow, this
     *       method will throw an exception after setting the bits
     *       since there is no way to check overflow without iterating
     *       twice through the bits array; This could be a desirable
     *       feature for arithmetic operations, so we will have to
     *       decide if we want to keep or change it later on
     *
     * @param bits An array of ints with bit positions to set
     *
     * @throws  IOException If setting bits causes overflow
     */
    public void setBits(int[] bits) throws IOException {
        boolean overflow = false;

        /* Zero out bitSet before beginning */
        this.set_zero();

        for (int i = 0; i < bits.length; i++) {
            /* Check for overflow */
            if (bits[i] >= this.get_size()) {
                overflow = true;
            } else {
                this.set(bits[i]);
            }
        }

        /* Throw exception if there was overflow */
        if (overflow) {
            String error = String.format("Overflow on register %s", name);
            throw new IOException(error);
        }
    }

    /**
     * This methods gets an array of ints with the position of the
     * bits currently set in the register
     *
     * @return array of ints with position of bits set in register
     */
    public int[] getSetBits() {
        if(this.cardinality() <=0){
            return null;
        }

        /* Get string representation of bit set */
        String bits = String.format("%16s", Integer.toBinaryString(this.read())).replace(' ', '0');
        System.out.printf("[Register::getSetBits] Have string representation from parent: %s\n", bits);

        return this.get_set_bits();
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
}
