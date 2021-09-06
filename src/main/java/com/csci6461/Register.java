/**
 * This file implements the register class for the CSCI 6461 project
 */
package com.csci6461;

/**
 * Import IOException, Arrays and BitSet class
 */
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

/**
 *
 * @author imanuelportalatin
 */
public class Register extends BitSet {
    /**
     * Name of the register being implemented
     */
    private String name;
    /**
     * Size of the register in bits
     */
    private int size;
    
    /**
     * Default constructor initializes name and size
     * 
     * @param name Name of the register being created
     * @param size Register size in bits
     */
    public Register(String n, int s) {
        super(s);
        
        System.out.printf("Creating register %s with size %s.\n", n, s);
        name = n;
        size = s;
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
        return size;
    }
    
    /**
     * This method loads a word of data into the register
     * 
     * @param data A byte array with each by to be loaded into register
     * 
     * @throws IOException If word is larger that register size
     */
    public void load(byte[] data) throws IOException {
        System.out.printf("[Register::load] Input data for register %s: %s\n", 
                name, Arrays.toString(data));
        /* Get a new bitset with the data provided */
        BitSet inputBits = BitSet.valueOf(data);
        
        /* Check for overflow */
        System.out.printf("[Register::load] Size = %d; Input bit length: %d\n", 
                size, inputBits.length());
        if (inputBits.length() > size) {
            /* Make sure any bits above register length are zeroed out */
            BitSet highBits = inputBits.get(size - 1, inputBits.length() - 1);
            System.out.printf("[Register::load] There are %d high bits: %s\n", 
                    highBits.length(), highBits.toString());
            if (highBits.cardinality() > 0) {
                String error = String.format("Overflow on register %s",name);
                throw new IOException(error);
            }
        }
        /* Make sure all bits are reset to 0 before loading new data */
        super.clear();
        
        /* Iterate through incoming data bits to find set bits */
        int i = 0;
        do {
            int nextSet = inputBits.nextSetBit(i);
            // System.out.printf("[Register::load] nextSet = %d, index = %d\n", 
            //         nextSet, i);
            if (nextSet >= 0) {
                super.set(nextSet);
                i = nextSet + 1;
            }
        } while (inputBits.nextSetBit(i) >= 0 && i < size);
        
        System.out.printf("New value of %s register: %s\n", name, super.toString());
    }
    
    /**
     * This methods gets the data stored in the register as a byte array
     * 
     * @returns data stored in the register as a byte array
     */
    public byte[] read() {
        return super.toByteArray();
    }
}